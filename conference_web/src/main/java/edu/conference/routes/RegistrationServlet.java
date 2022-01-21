package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.model.Section;
import edu.conference.model.User;
import edu.conference.model.builders.PaperBuilder;
import edu.conference.model.builders.UserBuilder;
import edu.conference.service.PaperService;
import edu.conference.service.SectionService;
import edu.conference.service.ServiceFactory;
import edu.conference.service.UserService;
import edu.conference.service.exception.ServiceException;
import edu.conference.utils.ModelFactory;
import edu.conference.utils.TemplateFactory;
import edu.conference.utils.Utility;
import edu.conference.utils.commands.CommandException;
import edu.conference.utils.commands.impl.UploadFileCommand;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.conference.utils.Constants.*;

@WebServlet("/registration")
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 5 * 5)
public class RegistrationServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationServlet.class);

    private UserService uService;
    private PaperService pService;
    private SectionService sService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServiceFactory factory = ServiceFactory.getInstance();
        uService = factory.getUserService();
        pService = factory.getPaperService();
        sService = factory.getSectionService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);
        List<Section> sections = sService.getAll();
        model.put("sections", sections);

        TemplateFactory.getTemplate("registration").apply(model, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = req.getParameter("reg-name").trim();
        String lastName = req.getParameter("reg-surname").trim();
        String pwd = req.getParameter("reg-pwd").trim();
        String pwdConf = req.getParameter("reg-pwd2").trim();
        String email = req.getParameter("reg-email").trim();
        String fac = req.getParameter("reg-fac").trim();
        String degree = req.getParameter("reg-academic-status");
        String position = req.getParameter("reg-position").trim();
        boolean isPresenting = "on".equals(req.getParameter("reg-is-presenting"));

        boolean successful = true;
        Map<String, Object> errors = new HashMap<>();

        if (Utility.containsInvalidMarkupCharacter(firstName)) {
            errors.put("name", true);
            successful = false;
        }

        if (Utility.containsInvalidMarkupCharacter(lastName)) {
            errors.put("surname", true);
            successful = false;
        }

        if (Utility.containsInvalidMarkupCharacter(fac)) {
            errors.put("fac", true);
            successful = false;
        }

        if (Utility.containsInvalidMarkupCharacter(position)) {
            errors.put("position", true);
            successful = false;
        }

        if (!email.matches(EMAIL_REGEXP)) {
            errors.put("email", true);
            successful = false;
        }

        if (!pwd.matches(PWD_REGEXP)) {
            errors.put("pwd", true);
            successful = false;
        }

        if (!pwd.equals(pwdConf)) {
            errors.put("pwdconf", true);
            successful = false;
        }

        HttpSession session = req.getSession();

        if (successful) {
            User user = new UserBuilder().withEmail(email).withFirstName(firstName).withLastName(lastName)
                    .withPwd(pwd).inInstitution(fac.length() == 0 ? null : fac).inPosition(position.trim().length() == 0 ? null : position).withDegree(degree)
                    .withRole(isPresenting ? "presenter" : "guest").build();

            try {
                if (!uService.register(user)) {
                    session.setAttribute("popups", new String[]{"Már létező felhasználó."});
                    resp.sendRedirect(req.getContextPath() + "/registration");
                    return;
                }
            } catch (ServiceException e) {
                LOG.error("Failed to register user {}.", user.getEmail());
                session.setAttribute("popups", new String[] {"Hiba történt, kérlek próbáld újra."});
                resp.sendRedirect(req.getContextPath() + "/registration");
                return;
            }

            if (isPresenting) {
                String title = req.getParameter("reg-title").trim();
                String section = req.getParameter("reg-section");
                String coAuthors = req.getParameter("reg-coauthors").trim();
                String abstr = req.getParameter("reg-abstract").trim();
                Part filePart = req.getPart("reg-file");

                if (filePart != null && !filePart.getContentType().equals(PDF_CONTENT_TYPE)) {
                    errors.put("file", "Nem megfelelő fájlkiterjesztés.");
                    successful = false;
                }

                Section concreteSection = sService.getByName(section);

                if (Utility.containsInvalidMarkupCharacter(title)) {
                    errors.put("title", true);
                    successful = false;
                }

                if (Utility.containsInvalidMarkupCharacter(coAuthors)) {
                    errors.put("coAuthors", true);
                    successful = false;
                }

                List<String> concreteCoAuthors = Arrays.stream(coAuthors.split(", "))
                        .dropWhile(elem -> elem.equals(" ") || elem.equals("")).toList();

                if (successful) {
                    Paper paper = new PaperBuilder().withTitle(title).withStatus("new")
                            .withAbstract(abstr).inSection(concreteSection)
                            .presents(user)
                            .withCoAuthors(concreteCoAuthors.size() != 0 ? concreteCoAuthors.toArray(new String[]{}) : null)
                            .build();

                    try {
                        paper = pService.register(paper);
                    } catch (ServiceException e) {
                        LOG.error("Failed to register paper {}.", paper.getTitle());
                        session.setAttribute("popups", new String[] {"Hiba történt, kérlek próbáld újra."});

                        // azonban ekkor a User már beszúrásra került, kikell törölni
                        try {
                            uService.delete(user.getId());
                        } catch (ServiceException ignored) {
                            LOG.error("Failed to delete user {} after paper registration failure.", user.getEmail());
                        }

                        resp.sendRedirect(req.getContextPath() + "/registration");
                        return;
                    }

                    if (filePart != null) {
                        try {
                            if (filePart.getContentType().equals(PDF_CONTENT_TYPE)) {
                                new UploadFileCommand(filePart, getServletContext(), paper).execute();
                                pService.update(paper);
                            } else {
                               LOG.error("User {} tried to upload a different file type {}.", user.getEmail(), filePart.getContentType());
                               session.setAttribute("popups", new String[]{"Dolgozat sikeresen regisztrálva, azonban hibás fájlttípus, próbáld újra a Profil-ból."});
                               resp.setStatus(406);
                               resp.sendRedirect(req.getContextPath() + "/index");
                               return;
                            }
                        } catch (CommandException e) {
                            LOG.error("Failed to upload document for paper {}.", paper.getId());
                            session.setAttribute("popups", new String[] {"Hiba történt a dokumentum feltöltésekor, jelentkezz be és próbáld újra."});
                            resp.sendRedirect(req.getContextPath() + "/index");
                            return;
                        }
                    }
                } else {
                    session.setAttribute("errors", errors);
                    resp.sendRedirect(req.getContextPath() + "/registration");
                    return;
                }
            }

            session.setAttribute("popups", new String[]{"Regisztráció sikeres!"});
            resp.sendRedirect(req.getContextPath() + "/index");
        } else {
            session.setAttribute("errors", errors);
            resp.sendRedirect(req.getContextPath() + "/registration");
        }
    }

}
