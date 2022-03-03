package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.model.Role;
import edu.conference.model.User;
import edu.conference.service.PaperService;
import edu.conference.service.ServiceFactory;
import edu.conference.service.exception.ServiceException;
import edu.conference.utils.ModelFactory;
import edu.conference.utils.commands.CommandException;
import edu.conference.utils.commands.impl.DeleteFileCommand;
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

import static edu.conference.utils.Utility.POPUP;
import static edu.conference.utils.Utility.alertRedirectUser;

@WebServlet({"/deletePaper", "/deletepaper"})
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
        if (!Role.ADMIN.equals(curr.getRole())) {
            LOG.warn("User {} without permissions tried to delete paper {}.", curr.getEmail(), tmpId);
            alertRedirectUser(req, resp, "Nem megfelelő jogosultságok.", 403, "/index");
            return;
        }

        long paperId;
        try {
            paperId = Long.parseLong(tmpId);
        } catch (NumberFormatException e) {
            LOG.error("Invalid paperId {}.", tmpId);
            alertRedirectUser(req, resp, "Hibás dolgozat azonosító.", 400, "/profile");
            return;
        }

        Paper paper;
        try {
            paper = pService.getById(paperId);
        } catch (ServiceException e) {
            LOG.error("Failed to access paper {}.", paperId);
            alertRedirectUser(req, resp, "Hiba történt, próbáld újra.", 500, "/profile");
            return;
        }

        try {
            pService.delete(paperId);
        } catch (ServiceException e) {
            LOG.error("Failed to access paper {}.", paperId);
            alertRedirectUser(req, resp, "Hiba történt, próbáld újra.", 500, "/profile");
            return;
        }

        try {
            new DeleteFileCommand(req, paper).execute();
        } catch (CommandException e) {
            LOG.error("Failed to delete paper {} phsyically from disk.", paperId);
            // nem ertesitsuk a felhasznalot
        }

        session.setAttribute(POPUP, new String[]{"Dolgozat sikeresen törölve."});
        resp.sendRedirect(req.getContextPath() + "/profile");
        LOG.info("Successfully deleted paper {} by user {}.", paperId, curr.getEmail());
    }

}
