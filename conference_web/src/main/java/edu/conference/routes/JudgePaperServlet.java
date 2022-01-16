package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.model.Role;
import edu.conference.model.Status;
import edu.conference.model.User;
import edu.conference.service.PaperService;
import edu.conference.service.ServiceFactory;
import edu.conference.utils.ModelFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet({"/verifypaper", "/verifyPaper"})
public class JudgePaperServlet extends HttpServlet {

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
            resp.setStatus(403);
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        Paper paper = pService.getById(paperId);
        paper.setStatus(Status.get(newStatus));
        pService.update(paper);
    }

}
