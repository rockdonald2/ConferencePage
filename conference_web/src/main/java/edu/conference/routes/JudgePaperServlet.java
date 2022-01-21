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

@WebServlet({"/verifypaper", "/verifyPaper"})
public class JudgePaperServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(JudgePaperServlet.class);

    private PaperService pService;

    @Override
    public void init() throws ServletException {
        super.init();
        pService = ServiceFactory.getInstance().getPaperService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);
        long paperId = Long.parseLong(req.getParameter("paperId"));
        User user = (User) model.get("user");
        String newStatus = req.getParameter("newStatus");

        if (!user.getRole().equals(Role.REPRESENTATIVE)) {
            LOG.error("Someone tried to access without authorization paper {}.", paperId);
            resp.setStatus(403);
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        HttpSession session = req.getSession();

        try {
            Paper paper = pService.getById(paperId);
            paper.setStatus(Status.get(newStatus));
            pService.update(paper);
            LOG.info("User {} successfully set new status {} for paper {}.", user.getEmail(), paper.getStatus(), paper.getId());
        } catch (ServiceException e) {
            LOG.error("Failed to judge paper {}.", paperId);
            session.setAttribute("popups", new String[]{"Hiba történt, próbáld újra."});
            resp.sendRedirect(req.getContextPath() + "/profile");
        }
    }

}
