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

@WebServlet("/revoke")
public class RevokeServlet extends HttpServlet {

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
            resp.setStatus(403);
            resp.sendRedirect(req.getContextPath() + "/index");
            return;
        }

        paper.setStatus(Status.NEW);
        paper.setDoc(null);
        pService.update(paper);

        resp.sendRedirect(req.getContextPath() + "/profile");
    }

}
