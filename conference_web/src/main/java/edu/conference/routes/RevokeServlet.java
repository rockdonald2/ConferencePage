package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.model.Role;
import edu.conference.model.Status;
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

@WebServlet("/revoke")
public class RevokeServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(RevokeServlet.class);

    private PaperService pService;

    @Override
    public void init() throws ServletException {
        super.init();
        pService = ServiceFactory.getInstance().getPaperService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);

        String path = req.getParameter("path");
        Paper paper = pService.getByPath(path);
        User curr = (User) model.get("user");

        if (!paper.getPresenter().getEmail().equals(curr.getEmail())
                && !paper.getSection().getRepresentative().getEmail().equals(curr.getEmail())
                && curr.getRole().equals(Role.ADMIN)) {
            LOG.error("Someone tried to access without authorization paper {}.", paper.getId());
            resp.setStatus(403);
            resp.sendRedirect(req.getContextPath() + "/index");
            return;
        }

        paper.setStatus(Status.NEW);
        paper.setDoc(null);

        HttpSession session = req.getSession();

        try {
            pService.update(paper);
        } catch (ServiceException e) {
            LOG.error("Failed to revoke paper {}.", paper.getId());
            session.setAttribute("popups", new String[] {"Hiba történt, próbáld újra."});
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        session.setAttribute("popups", new String[]{"Dolgozat sikeresen visszavonva."});
        resp.sendRedirect(req.getContextPath() + "/profile");
    }

}
