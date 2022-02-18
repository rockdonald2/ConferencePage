package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.model.Section;
import edu.conference.model.User;
import edu.conference.service.PaperService;
import edu.conference.service.SectionService;
import edu.conference.service.ServiceFactory;
import edu.conference.service.UserService;
import edu.conference.service.exception.ServiceException;
import edu.conference.utils.ModelFactory;
import edu.conference.utils.TemplateFactory;
import edu.conference.utils.Utility;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.conference.utils.Utility.*;

@WebServlet("/modify")
public class ModifyServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ModifyServlet.class);

    private PaperService pService;
    private SectionService sService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServiceFactory factory = ServiceFactory.getInstance();
        pService = factory.getPaperService();
        sService = factory.getSectionService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);
        User user = (User) model.get("user");

        String tmpId = req.getParameter("paperId");
        if (tmpId == null) {
            LOG.error("Access for paperId came without paperId specified.");
            alertRedirectUser(req, resp, "Helytelen kérés.", 400, "/profile");
            return;
        }

        long paperId;
        try {
            paperId = Long.parseLong(tmpId);
        } catch (NumberFormatException e) {
            LOG.error("Access for paperId came with unparseable paperId specified.");
            alertRedirectUser(req, resp, "Helytelen kérés.", 400, "/profile");
            return;
        }

        Paper paper;
        try {
            paper = pService.getById(paperId);
        } catch (ServiceException e) {
            LOG.info("User {} tried to query invalid paper with id {}.", user.getEmail(), paperId);
            alertRedirectUser(req, resp, "Nem létező dolgozat.", 404, "/profile");
            return;
        }

        if (!paper.getPresenter().equals(user)) {
            LOG.warn("Someone without permissions tried to modify users {}.", user.getEmail());
            alertRedirectUser(req, resp, "Nincs megfelelő jogosultsága.", 403, "/profile");
            return;
        }

        List<Section> sections;
        try {
            sections = sService.getAll();
        } catch (ServiceException e) {
            LOG.error("Failed to query all sections.");
            alertRedirectUser(req, resp, "Hiba történt, próbáld újra.", 500, "/profile");
            return;
        }

        model.put("paper", paper);
        model.put("coAuthors", paper.getCoAuthors() != null ? String.join(", ", paper.getCoAuthors()) : "");
        model.put("sections", sections);
        TemplateFactory.getTemplate("modify").apply(model, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);
        HttpSession session = req.getSession();

        long paperId;
        try {
            paperId = Long.parseLong(req.getParameter("modify-paperId"));
        } catch (NumberFormatException e) {
            LOG.error("Invalid paper id {}.", req.getParameter("modify-paperId"));
            alertRedirectUser(req, resp, "Helytelen kérés.", 400, "/profile");
            return;
        }

        Paper paper;
        try {
            paper = pService.getById(paperId);
        } catch (ServiceException e) {
            LOG.error("Non-existing paper with id {}.", paperId);
            alertRedirectUser(req, resp, "Nem létező dolgozat.", 404, "/profile");
            return;
        }

        User user = (User) model.get("user");
        if (!paper.getPresenter().equals(user)) {
            LOG.error("Someone {} tried to modify paper {} without permissions.", user.getEmail(), paper.getId());
            alertRedirectUser(req, resp, "Nincs megfelelő jogosultsága.", 403, "/profile");
            return;
        }

        String title = req.getParameter("modify-title").trim();
        List<String> coAuthors = Arrays.stream(req.getParameter("modify-coauthors").split(", "))
                .dropWhile(elem -> elem.equals(" ") || elem.equals("")).toList();
        String abstr = req.getParameter("modify-abstract").trim();

        Section section;
        try {
            section = sService.getByName(req.getParameter("modify-section").trim());
        } catch (ServiceException e) {
            LOG.error("Invalid section {}.", req.getParameter("modify-section"));
            alertRedirectUser(req, resp, "Helytelen kérés.", 400, "/profile");
            return;
        }

        boolean successful = true;
        Map<String, Boolean> errors = new HashMap<>();

        if (Utility.containsInvalidMarkupCharacter(title)) {
            successful = false;
            errors.put("title", true);
        }

        if (Utility.containsInvalidMarkupCharacter(abstr)) {
            successful = false;
            errors.put("abstr", true);
        }

        if (successful) {
            paper.setAbstr(abstr);
            paper.setTitle(title);
            paper.setCoAuthors(coAuthors.size() != 0 ? coAuthors.toArray(new String[]{}) : null);
            paper.setSection(section);

            try {
                pService.update(paper);
            } catch (ServiceException e) {
                LOG.error("Failed to update paper {} by user {}.", paper.getId(), user.getEmail());
                alertRedirectUser(req, resp, "Hiba történt, próbáld újra.", 500, "/index");
                return;
            }

            session.setAttribute(POPUP, new String[]{"Dolgozat sikeresen frissítve."});
            resp.sendRedirect(req.getContextPath() + "/profile");
            LOG.info("User {} successfully modified paper {}.", user.getEmail(), paper.getId());
        } else {
            session.setAttribute(ERROR, errors);
            resp.sendRedirect(req.getContextPath() + "/modify?paperId=" + paperId);
        }
    }

}
