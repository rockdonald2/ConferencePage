package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.model.Role;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {

    private static final int SIZE = 1024;

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

        resp.setContentType("text/plain");
        resp.setHeader("Content-disposition", "attachment; filename=" + path);

        try(InputStream in = req.getServletContext().getResourceAsStream("/uploads/" + path);
            OutputStream out = resp.getOutputStream()) {

            byte[] buffer = new byte[SIZE];

            int numBytesRead;
            while ((numBytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, numBytesRead);
            }
        }
    }

}
