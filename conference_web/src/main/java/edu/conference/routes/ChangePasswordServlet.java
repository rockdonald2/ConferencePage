package edu.conference.routes;

import edu.conference.model.User;
import edu.conference.service.ServiceFactory;
import edu.conference.service.UserService;
import edu.conference.service.exception.ServiceException;
import edu.conference.util.PasswordEncrypter;
import edu.conference.utils.ModelFactory;
import edu.conference.utils.TemplateFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static edu.conference.utils.Constants.PWD_REGEXP;
import static edu.conference.utils.Utility.*;

@WebServlet("/changepassword")
public class ChangePasswordServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ChangePasswordServlet.class);

    private UserService uService;

    @Override
    public void init() throws ServletException {
        super.init();
        uService = ServiceFactory.getInstance().getUserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateFactory.getTemplate("changepassword").apply(ModelFactory.createModel(req), resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);
        User user = (User) model.get("user");

        HttpSession session = req.getSession();

        try {
            user = uService.getById(user.getId());
        } catch (ServiceException e) {
            LOG.error("Failed to get user {}.", user.getEmail());
            alertRedirectUser(req, resp, "Hiba történt, próbáld újra.", 500, "/changepassword");
            return;
        }

        String oldPwd = req.getParameter("changepwd-old");
        String newPwd = req.getParameter("changepwd-new");
        String newConf = req.getParameter("changepwd-newconf");

        Map<String, Boolean> errors = new HashMap<>();

        try {
            String oldEncrpyted = PasswordEncrypter.generateHashedPassword(oldPwd, user.getUuid());
            boolean successful = true;

            if (!user.getPwd().equals(oldEncrpyted)) {
                errors.put("old", true);
                successful = false;
            }

            if (!newPwd.matches(PWD_REGEXP)) {
                errors.put("new", true);
                successful = false;
            }

            if (!newPwd.equals(newConf)) {
                errors.put("newconf", true);
                successful = false;
            }

            if (!successful) {
                resp.setStatus(400);
                resp.sendRedirect(req.getContextPath() + "/changepassword");
                session.setAttribute(ERROR, errors);
                return;
            }

            user.setPwd(newPwd);

            try {
                uService.changePwd(user);
            } catch (ServiceException e) {
                LOG.error("Failed to change password for user {}.", user.getEmail());
                alertRedirectUser(req, resp, "Hiba történt, próbáld újra.", 500, "/changepassword");
                return;
            }

            session.invalidate();
            session = req.getSession();
            session.setAttribute(POPUP, new String[]{"Jelszó sikeresen megváltoztatva."});
            resp.sendRedirect(req.getContextPath() + "/index");
            LOG.info("User {} successfully changed password.", user.getEmail());
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Invalid algorithm for encryption.");
            alertRedirectUser(req, resp, "Hiba történt, próbáld újra.", 500, "/500");
        }
    }

}
