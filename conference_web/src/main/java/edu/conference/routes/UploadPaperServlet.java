package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.service.PaperService;
import edu.conference.service.ServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

@WebServlet({"/uploadpaper", "/uploadPaper"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadPaperServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(UploadPaperServlet.class);
    private static final String UPLOAD_DIRECTORY = "uploads";
    private static final String PDF_SUFFIX = ".pdf";
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    private PaperService pService;
    private String uploadPath;

    @Override
    public void init() throws ServletException {
        super.init();
        pService = ServiceFactory.getInstance().getPaperService();

        uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Part filePart = req.getPart("paper-file");

        if (!filePart.getContentType().equals(PDF_CONTENT_TYPE)) {
            resp.setStatus(406);
        } else {
            long paperId = Long.parseLong(req.getParameter("paper-id"));
            Paper paper = pService.getById(paperId);
            String fileName = paper.getUuid() + PDF_SUFFIX;
            filePart.write(uploadPath + File.separator + fileName);
            paper.setDoc(fileName);
            pService.update(paper);
        }

        resp.sendRedirect(req.getContextPath() + "/profile");
    }

}
