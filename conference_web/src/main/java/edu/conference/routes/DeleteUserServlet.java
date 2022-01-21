package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.model.Role;
import edu.conference.model.User;
import edu.conference.service.PaperService;
import edu.conference.service.ServiceFactory;
import edu.conference.service.UserService;
import edu.conference.service.exception.ServiceException;
import edu.conference.utils.ModelFactory;
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

@WebServlet({"/deleteuser", "/deleteUser"})
public class DeleteUserServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteUserServlet.class);

    private UserService uService;
    private PaperService pService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServiceFactory factory = ServiceFactory.getInstance();
        uService = factory.getUserService();
        pService = factory.getPaperService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);
        User curr = (User) model.get("user");

        HttpSession session = req.getSession();
        if (!Role.ADMIN.equals(curr.getRole())) {
            LOG.warn("Someone tried to access admin resource {}.", curr.getEmail());
            session.setAttribute("popups", new String[]{"Nem megfelelő jogosultságok."});
            resp.setStatus(403);
            resp.sendRedirect(req.getContextPath() + "/index");
            return;
        }

        String tmpId = req.getParameter("userId");

        long userId;
        try {
            userId = Long.parseLong(tmpId);
        } catch (NumberFormatException e) {
            LOG.error("Invalid userId {}.", tmpId);
            session.setAttribute("popups", new String[]{"Hibás felhasználói azonosító."});
            resp.setStatus(400);
            resp.sendRedirect(req.getContextPath() + "/users");
            return;
        }

        User user;
        try {
            user = uService.getById(userId);
        } catch (ServiceException e) {
            LOG.error("Failed to access user {}.", userId);
            session.setAttribute("popups", new String[]{"Nem létező felhasználó vagy hiba történt."});
            resp.setStatus(404);
            resp.sendRedirect(req.getContextPath() + "/users");
            return;
        }

        List<Paper> papers;
        try {
            papers = pService.getAllForPresenter(user.getEmail());
        } catch (ServiceException e) {
            LOG.error("Failed to access papers for user {}.", user.getEmail());
            session.setAttribute("popups", new String[]{"Hiba történt, próbáld újra."});
            resp.setStatus(500);
            resp.sendRedirect(req.getContextPath() + "/users");
            return;
        }

        try {
            papers.forEach(paper -> pService.delete(paper.getId()));
        } catch (ServiceException e) {
            LOG.error("Failed to delete papers for user {}.", user.getEmail());
            session.setAttribute("popups", new String[]{"Hiba történt, próbáld újra."});
            resp.setStatus(500);
            resp.sendRedirect(req.getContextPath() + "/users");
            return;
        }

        try {
            uService.delete(userId);
        } catch (ServiceException e) {
            LOG.error("Failed to delete user {}.", userId);
            session.setAttribute("popups", new String[]{"Hiba történt, próbáld újra."});
            resp.setStatus(500);
            resp.sendRedirect(req.getContextPath() + "/users");
            return;
        }

        session.setAttribute("popups", new String[]{"Felhasználó sikeresen törölve."});
        resp.sendRedirect(req.getContextPath() + "/users");
        LOG.info("Successfully deleted user {} by user {}.", user.getEmail(), curr.getEmail());
    }

}
