package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.model.Role;
import edu.conference.model.User;
import edu.conference.service.PaperService;
import edu.conference.service.ServiceFactory;
import edu.conference.service.UserService;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileServlet.class);

    private UserService uService;
    private PaperService pService;

    @Override
    public void init() throws ServletException {
        super.init();
        uService = ServiceFactory.getInstance().getUserService();
        pService = ServiceFactory.getInstance().getPaperService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);

        User curr = (User) model.get("user");
        boolean todoStatusVal = true;
        List<Paper> papers = Collections.emptyList();
        if (curr.getRole().equals(Role.PRESENTER)) {
            todoStatusVal = uService.arePapersUploaded(curr);
            papers = pService.getAllForPresenter(curr.getEmail());
        }

        model.put("todoStatus", todoStatusVal);
        model.put("papers", papers);

        TemplateFactory.getTemplate("profile").apply(model, resp.getWriter());
    }

}
