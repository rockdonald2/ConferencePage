package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.model.Role;
import edu.conference.model.User;
import edu.conference.service.PaperService;
import edu.conference.service.ServiceFactory;
import edu.conference.service.exception.ServiceException;
import edu.conference.utils.ModelFactory;
import edu.conference.utils.commands.CommandException;
import edu.conference.utils.commands.impl.DownloadFileCommand;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(DownloadServlet.class);

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

        HttpSession session = req.getSession();

        Paper paper;
        try {
            paper = pService.getByPath(path);
        } catch (ServiceException e) {
            LOG.error("Failed to get paper by path {}.", path);
            session.setAttribute("popups", new String[] {"Hiba történt, próbáld újra."});
            resp.sendRedirect(req.getContextPath() + "/index");
            return;
        }

        User curr = (User) model.get("user");

        if (!paper.getPresenter().getEmail().equals(curr.getEmail())
                && !paper.getSection().getRepresentative().getEmail().equals(curr.getEmail())
                && !Role.ADMIN.equals(curr.getRole())) {
            LOG.error("Someone tried to access without authorization paper {}.", paper.getId());
            resp.setStatus(403);
            resp.sendRedirect(req.getContextPath() + "/index");
            return;
        }

        try {
            new DownloadFileCommand(resp, req, path).execute();
            LOG.info("Successfully deleted paper {} by user {}.", paper.getId(), curr.getEmail());
        } catch (CommandException e) {
            session.setAttribute("popups", new String[] {"Hiba történt, próbáld újra."});
            resp.sendRedirect(req.getContextPath() + "/profile");
        }
    }

}
