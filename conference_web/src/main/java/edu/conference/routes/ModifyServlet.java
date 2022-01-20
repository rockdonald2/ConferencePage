package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.model.Section;
import edu.conference.model.User;
import edu.conference.model.builders.PaperBuilder;
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

@WebServlet("/modify")
public class ModifyServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ModifyServlet.class);

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
        HttpSession session = req.getSession();

        String tmpId = req.getParameter("paperId");
        if (tmpId == null) {
            session.setAttribute("popups", new String[]{"Helytelen kérés."});
            resp.setStatus(400);
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        long paperId;
        try {
            paperId = Long.parseLong(tmpId);
        } catch (NumberFormatException e) {
            session.setAttribute("popups", new String[]{"Helytelen kérés."});
            resp.setStatus(400);
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        Paper paper;
        try {
            paper = pService.getById(paperId);
        } catch (ServiceException e) {
            session.setAttribute("popups", new String[]{"Nem létező dolgozat."});
            resp.setStatus(404);
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        User user = (User) model.get("user");
        if (!paper.getPresenter().equals(user)) {
            session.setAttribute("popups", new String[]{"Nincs megfelelő jogosultsága."});
            resp.setStatus(403);
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        List<Section> sections;
        try {
            sections = sService.getAll();
        } catch (ServiceException e) {
            session.setAttribute("popups", new String[]{"Hiba történt."});
            resp.setStatus(500);
            resp.sendRedirect(req.getContextPath() + "/profile");
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
            session.setAttribute("popups", new String[]{"Helytelen kérés."});
            resp.setStatus(400);
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        Paper paper;
        try {
            paper = pService.getById(paperId);
        } catch (ServiceException e) {
            session.setAttribute("popups", new String[]{"Nem létező dolgozat."});
            resp.setStatus(404);
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        User user = (User) model.get("user");
        if (!paper.getPresenter().equals(user)) {
            session.setAttribute("popups", new String[]{"Nincs megfelelő jogosultsága."});
            resp.setStatus(403);
            resp.sendRedirect(req.getContextPath() + "/profile");
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
            session.setAttribute("popups", new String[]{"Hibás szekció."});
            resp.setStatus(400);
            resp.sendRedirect(req.getContextPath() + "/profile");
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
                session.setAttribute("popups", new String[]{"Hiba történt, próbáld újra."});
            }

            session.setAttribute("popups", new String[]{"Dolgozat sikeresen frissítve."});
            resp.sendRedirect(req.getContextPath() + "/profile");
        } else {
            session.setAttribute("errors", errors);
            resp.sendRedirect(req.getContextPath() + "/modify?paperId=" + paperId);
        }
    }

}
