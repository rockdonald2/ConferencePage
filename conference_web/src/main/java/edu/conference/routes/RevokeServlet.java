package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.model.Role;
import edu.conference.model.Status;
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

        HttpSession session = req.getSession();

        if (!paper.getPresenter().getEmail().equals(curr.getEmail())
                && !paper.getSection().getRepresentative().getEmail().equals(curr.getEmail())
                && !Role.ADMIN.equals(curr.getRole())) {
            LOG.error("Someone tried to access without authorization paper {}.", paper.getId());
            alertRedirectUser(req, resp, "Nincs megfelelő jogosultsága.", 403, "/profile");
            return;
        }

        paper.setStatus(Status.NEW);
        paper.setDoc(null);

        try {
            new DeleteFileCommand(req, paper).execute();
        } catch (CommandException e) {
            LOG.error("Failed to delete paper {} phsyically from disk.", paper.getId());
            // nem ertesitsuk a felhasznalot
        }

        try {
            pService.update(paper);
        } catch (ServiceException e) {
            LOG.error("Failed to revoke paper {}.", paper.getId());
            alertRedirectUser(req, resp, "Hiba történt, próbáld újra.", 500, "/profile");
            return;
        }

        session.setAttribute(POPUP, new String[]{"Dolgozat sikeresen visszavonva."});
        resp.sendRedirect(req.getContextPath() + "/profile");
        LOG.info("User {} successfully revoked paper {}.", curr.getEmail(), paper.getId());
    }

}
