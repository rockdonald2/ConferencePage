package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.service.PaperService;
import edu.conference.service.ServiceFactory;
import edu.conference.service.exception.ServiceException;
import edu.conference.utils.Utility;
import edu.conference.utils.commands.CommandException;
import edu.conference.utils.commands.impl.UploadFileCommand;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static edu.conference.utils.Utility.POPUP;
import static edu.conference.utils.Utility.alertRedirectUser;

@WebServlet({"/uploadpaper", "/uploadPaper"})
@MultipartConfig(maxFileSize = 1024 * 1024 * 5 * 5 * 2,
        maxRequestSize = 1024 * 1024 * 5 * 5 * 2)
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
        HttpSession session = req.getSession();

        if (Utility.isEmptyFile(filePart) || !Utility.isPdfFile(filePart)) {
            LOG.warn("Invalid file type {} tried to be uploaded.", filePart.getContentType());
            alertRedirectUser(req, resp, "Hiba történt, nem megfelelő fájltípus, próbáld újra.", 406, "/profile");
            return;
        } else {
            long paperId = Long.parseLong(req.getParameter("paper-id"));
            Paper paper = pService.getById(paperId);

            try {
                new UploadFileCommand(filePart, getServletContext(), paper).execute();
            } catch (CommandException e) {
                LOG.error("Failed to upload document for paper {}.", paper.getId());
                alertRedirectUser(req, resp, "Hiba történt, próbáld újra.", 500, "/profile");
                return;
            }

            try {
                pService.update(paper);
            } catch (ServiceException e) {
                LOG.error("Failed to upload document for paper {}.", paper.getId());
                alertRedirectUser(req, resp, "Hiba történt, próbáld újra.", 500, "/profile");
                return;
            }

            session.setAttribute(POPUP, new String[]{"Dolgozat sikeresen feltöltve."});
            LOG.info("New paper successfully uploaded on path {}.", paper.getDoc());
        }

        resp.sendRedirect(req.getContextPath() + "/profile");
    }

}
