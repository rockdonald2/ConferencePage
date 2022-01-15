package edu.conference.routes;

import edu.conference.model.User;
import edu.conference.service.ServiceFactory;
import edu.conference.service.UserService;
import edu.conference.utils.TemplateFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

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
        User requestedLogin = new User(req.getParameter("loginEmail"), req.getParameter("loginPwd"));
        boolean successful = uService.login(requestedLogin);
        requestedLogin = uService.getByEmail(requestedLogin.getEmail());

        HttpSession session = req.getSession();
        if (successful) {
            session.setAttribute("email", requestedLogin.getEmail());
            session.setAttribute("role", requestedLogin.getRole().toString());
            resp.sendRedirect(req.getContextPath() + "/index");
        } else {
            Map<String, Object> model = new ConcurrentHashMap<>();
            model.put("loginError", true);
            TemplateFactory.getTemplate("login").apply(model, resp.getWriter());
        }
    }

}
