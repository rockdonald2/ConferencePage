package edu.conference.routes;

import edu.conference.model.Role;
import edu.conference.model.User;
import edu.conference.service.ServiceFactory;
import edu.conference.service.UserService;
import edu.conference.service.exception.ServiceException;
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
import java.util.List;
import java.util.Map;


@WebServlet("/users")
public class UsersServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(UsersServlet.class);
    private static final int PAGE_SIZE = 4;

    private UserService uService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServiceFactory factory = ServiceFactory.getInstance();
        uService = factory.getUserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);
        User curr = (User) model.get("user");

        HttpSession session = req.getSession();
        if (!Role.ADMIN.equals(curr.getRole())) {
            LOG.warn("Someone tried to access admin resource {}.", curr.getEmail());
            session.setAttribute("popups", new String[]{"Nem megfelelő jogosultságok."});
            resp.setStatus(403);
            resp.sendRedirect(req.getContextPath() + "/index");
            return;
        }

        List<User> users;
        try {
            users = uService.getAll().stream().filter(user -> !user.getEmail().equals(curr.getEmail())).toList();
        } catch (ServiceException e) {
            LOG.error("Failed to access users.");
            session.setAttribute("popups", new String[]{"Hiba történt."});
            resp.setStatus(500);
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        String currQuery = req.getParameter("query");
        String currPage = req.getParameter("page");

        String query = currQuery == null ? "" : currQuery;

        if (!query.equals("")) {
            users = users.stream().filter(user -> (user.getLastName() + user.getFirstName()).contains(query)).toList();
        }

        int page;
        boolean onFirstPage;
        boolean onLastPage;
        if (users.size() == 0) {
            page = 1;
            onFirstPage = true;
            onLastPage = true;
        } else {

            page = currPage == null ? 1 : Integer.parseInt(currPage);
            int maxPages = (int) Math.ceil((double) users.size() / PAGE_SIZE);

            onFirstPage = page == 1;
            onLastPage = page == maxPages;

            if (page > maxPages) {
                session.setAttribute("popups", new String[]{"Nem létező oldalszám."});
                resp.setStatus(400);
                resp.sendRedirect(req.getContextPath() + "/users");
                return;
            }

            users = users.stream().skip((long) (page - 1) * PAGE_SIZE)
                    .limit(PAGE_SIZE).toList();
        }

        model.put("lastQuery", query);
        model.put("currPage", page);
        model.put("onFirstPage", onFirstPage);
        model.put("onLastPage", onLastPage);
        model.put("users", users);
        TemplateFactory.getTemplate("users").apply(model, resp.getWriter());
    }

}
