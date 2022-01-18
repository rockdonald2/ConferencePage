package edu.conference.routes;

import edu.conference.model.Role;
import edu.conference.model.Section;
import edu.conference.model.User;
import edu.conference.model.builders.SectionBuilder;
import edu.conference.service.SectionService;
import edu.conference.service.ServiceFactory;
import edu.conference.service.UserService;
import edu.conference.service.exception.ServiceException;
import edu.conference.utils.ModelFactory;
import edu.conference.utils.TemplateFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet({"/createsection", "/createSection"})
public class CreateSectionServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(CreateSectionServlet.class);

    private SectionService sService;
    private UserService uService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServiceFactory factory = ServiceFactory.getInstance();
        sService = factory.getSectionService();
        uService = factory.getUserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);

        User curr = (User) model.get("user");

        if (!curr.getRole().equals(Role.ADMIN)) {
            HttpSession session = req.getSession();
            session.setAttribute("popups", new String[]{"Nem megfelelő jogosultságok."});
            resp.setStatus(403);
            resp.sendRedirect(req.getContextPath() + "/index");
            return;
        }

        List<Section> sections = sService.getAll();
        Set<User> representatives = sections.stream().map(Section::getRepresentative).collect(Collectors.toSet());
        model.put("representatives", representatives);

        TemplateFactory.getTemplate("createsection").apply(model, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);

        User curr = (User) model.get("user");

        HttpSession session = req.getSession();

        if (!curr.getRole().equals(Role.ADMIN)) {
            session.setAttribute("popups", new String[]{"Nem megfelelő jogosultságok."});
            resp.setStatus(403);
            resp.sendRedirect(req.getContextPath() + "/index");
            return;
        }

        User user;
        try {
            user = uService.getByEmail(req.getParameter("cr-rep"));
        } catch (ServiceException e) {
            LOG.error("Failed to find representative by email {}.", req.getParameter("cr-rep"));
            session.setAttribute("popups", new String[]{"Hiba történt."});
            resp.sendRedirect(req.getContextPath() + "/createsection");
            return;
        }

        Section section = new SectionBuilder().withName(req.getParameter("cr-name").trim()).withDescription(req.getParameter("cr-desc").trim())
                .withRepresentative(user).build();

        try {
            sService.create(section);
        } catch (ServiceException e) {
            LOG.error("Failed to create section {}.", section.getName());
            session.setAttribute("popups", new String[]{"Hiba történt."});
            resp.sendRedirect(req.getContextPath() + "/createsection");
            return;
        }

        session.setAttribute("popups", new String[]{"Sikeresen létrehozva a " + section.getName() + " szekció."});
        resp.sendRedirect(req.getContextPath() + "/profile");
    }

}
