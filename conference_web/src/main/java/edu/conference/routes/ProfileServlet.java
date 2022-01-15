package edu.conference.routes;

import edu.conference.model.Role;
import edu.conference.model.User;
import edu.conference.service.ServiceFactory;
import edu.conference.service.UserService;
import edu.conference.utils.ModelFactory;
import edu.conference.utils.TemplateFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private UserService uService;

    @Override
    public void init() throws ServletException {
        super.init();
        uService = ServiceFactory.getInstance().getUserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);

        User curr = (User) model.get("user");

        boolean todoStatusVal = true;
        if (curr.getRole().equals(Role.PRESENTER)) {
            todoStatusVal = uService.arePapersUploaded(curr);
        }

        model.put("todoStatus", todoStatusVal);

        TemplateFactory.getTemplate("profile").apply(model, resp.getWriter());
    }

}
