package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.service.PaperService;
import edu.conference.service.ServiceFactory;
import edu.conference.utils.commands.impl.UploadFileCommand;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static edu.conference.utils.Constants.*;

@WebServlet({"/uploadpaper", "/uploadPaper"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadPaperServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(UploadPaperServlet.class);

    private PaperService pService;

    @Override
    public void init() throws ServletException {
        super.init();
        pService = ServiceFactory.getInstance().getPaperService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Part filePart = req.getPart("paper-file");

        if (!filePart.getContentType().equals(PDF_CONTENT_TYPE)) {
            resp.setStatus(406);
        } else {
            long paperId = Long.parseLong(req.getParameter("paper-id"));
            Paper paper = pService.getById(paperId);

            new UploadFileCommand(filePart, getServletContext(), paper).execute();

            HttpSession session = req.getSession();
            session.setAttribute("popups", new String[]{"Dolgozat sikeresen feltöltve."});

            pService.update(paper);
        }

        resp.sendRedirect(req.getContextPath() + "/profile");
    }

}
