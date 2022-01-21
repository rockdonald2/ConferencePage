package edu.conference.routes;

import edu.conference.model.User;
import edu.conference.model.builders.UserBuilder;
import edu.conference.service.ServiceFactory;
import edu.conference.service.UserService;
import edu.conference.service.exception.ServiceException;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(LoginServlet.class);

    private UserService uService;

    @Override
    public void init() throws ServletException {
        super.init();
        uService = ServiceFactory.getInstance().getUserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateFactory.getTemplate("login").apply(null, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User requestedLogin = new UserBuilder().withEmail(req.getParameter("loginEmail")).withPwd(req.getParameter("loginPwd")).build();

        HttpSession session = req.getSession();

        boolean successful;
        try {
            successful = uService.login(requestedLogin);
            requestedLogin = uService.getByEmail(requestedLogin.getEmail());
        } catch (ServiceException e) {
            LOG.error("Failed to login user {}.", requestedLogin.getEmail());
            session.setAttribute("popups", new String[] {"Hiba történt, próbáld újra."});
            resp.sendRedirect(req.getContextPath() + "/index");
            return;
        }

        requestedLogin.setPwd(null);

        if (successful) {
            session.setAttribute("logged", true);
            session.setAttribute("user", requestedLogin);
            session.setAttribute("popups", new String[] {"Sikeres bejelentkezés."});
            resp.sendRedirect(req.getContextPath() + "/index");
            LOG.info("User {} successfully logged in.", requestedLogin.getEmail());
        } else {
            Map<String, Object> model = new ConcurrentHashMap<>();
            model.put("loginError", true);
            TemplateFactory.getTemplate("login").apply(model, resp.getWriter());
            LOG.info("Unsuccessful login for user {}.", requestedLogin.getEmail());
        }
    }

}
