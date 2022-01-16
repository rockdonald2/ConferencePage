package edu.conference.routes;

import edu.conference.model.User;
import edu.conference.service.ServiceFactory;
import edu.conference.service.UserService;
import edu.conference.util.PasswordEncrypter;
import edu.conference.utils.ModelFactory;
import edu.conference.utils.TemplateFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

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

        user = uService.getById(user.getId());

        String oldPwd = req.getParameter("changepwd-old");
        String newPwd = req.getParameter("changepwd-new");
        String newConf = req.getParameter("changepwd-newconf");

        try {
            String oldEncrpyted = PasswordEncrypter.generateHashedPassword(oldPwd, user.getUuid());

            if (!user.getPwd().equals(oldEncrpyted)) {
                resp.setStatus(400);
                resp.sendRedirect(req.getContextPath() + "/changepassword");
                return;
            }

            if (!newPwd.equals(newConf)) {
                resp.setStatus(400);
                resp.sendRedirect(req.getContextPath() + "/changepassword");
                return;
            }

            user.setPwd(newPwd);
            uService.changePwd(user);
            resp.sendRedirect(req.getContextPath() + "/logout");
        } catch (NoSuchAlgorithmException e) {
            throw new ServletException();
        }
    }

}
