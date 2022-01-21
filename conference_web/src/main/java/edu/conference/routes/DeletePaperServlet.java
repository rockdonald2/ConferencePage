package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.model.Role;
import edu.conference.model.User;
import edu.conference.service.PaperService;
import edu.conference.service.ServiceFactory;
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
import java.util.Map;

@WebServlet("/delete")
public class DeletePaperServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(DeletePaperServlet.class);

    private PaperService pService;

    @Override
    public void init() throws ServletException {
        super.init();
        pService = ServiceFactory.getInstance().getPaperService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);
        User curr = (User) model.get("user");
        String tmpId = req.getParameter("paperId");

        HttpSession session = req.getSession();
        if (!curr.getRole().equals(Role.ADMIN)) {
            LOG.warn("User {} without permissions tried to delete paper {}.", curr.getEmail(), tmpId);
            session.setAttribute("popups", new String[]{"Nem megfelelő jogosultságok."});
            resp.setStatus(403);
            resp.sendRedirect(req.getContextPath() + "/index");
            return;
        }

        long paperId;
        try {
            paperId = Long.parseLong(tmpId);
        } catch (NumberFormatException e) {
            LOG.error("Invalid paperId {}.", tmpId);
            session.setAttribute("popups", new String[]{"Hibás dolgozat azonosító."});
            resp.setStatus(400);
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        try {
            pService.delete(paperId);
        } catch (ServiceException e) {
            LOG.error("Failed to access paper {}.", paperId);
            session.setAttribute("popups", new String[]{"Hiba történt."});
            resp.setStatus(500);
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        session.setAttribute("popups", new String[]{"Dolgozat sikeresen törölve."});
        resp.sendRedirect(req.getContextPath() + "/profile");
    }

}
