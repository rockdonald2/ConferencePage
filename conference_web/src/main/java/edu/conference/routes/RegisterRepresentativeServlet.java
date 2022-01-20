package edu.conference.routes;

import edu.conference.model.ModelException;
import edu.conference.model.Role;
import edu.conference.model.User;
import edu.conference.model.builders.UserBuilder;
import edu.conference.service.ServiceFactory;
import edu.conference.service.UserService;
import edu.conference.service.exception.ServiceException;
import edu.conference.utils.ModelFactory;
import edu.conference.utils.TemplateFactory;
import edu.conference.utils.Utility;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static edu.conference.utils.Constants.EMAIL_REGEXP;

@WebServlet({"/registerrepresentative", "/registerRepresentative"})
public class RegisterRepresentativeServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterRepresentativeServlet.class);

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

        if (!curr.getRole().equals(Role.ADMIN)) {
            HttpSession session = req.getSession();
            session.setAttribute("popups", new String[]{"Nem megfelelő jogosultságok."});
            resp.setStatus(403);
            resp.sendRedirect(req.getContextPath() + "/index");
            return;
        }

        TemplateFactory.getTemplate("registerrepresentative").apply(model, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);
        User curr = (User) model.get("user");

        HttpSession session = req.getSession();
        if (!curr.getRole().equals(Role.ADMIN)) {
            session.setAttribute("popups", new String[]{"Nem megfelelő jogosultságok."});
            resp.setStatus(403);
            resp.sendRedirect(req.getContextPath() + "/index");
            return;
        }

        String firstName = req.getParameter("regrep-name");
        String lastName = req.getParameter("regrep-surname");
        String email = req.getParameter("regrep-email");
        String fac = req.getParameter("regrep-fac");
        String position = req.getParameter("regrep-pos");
        String degree = req.getParameter("regrep-status");

        boolean successful = true;
        Map<String, Boolean> errors = new HashMap<>();

        if (firstName == null || Utility.containsInvalidMarkupCharacter(firstName)) {
            successful = false;
            errors.put("name", true);
        }

        if (lastName == null || Utility.containsInvalidMarkupCharacter(lastName)) {
            successful = false;
            errors.put("surname", true);
        }

        if (email == null || !email.matches(EMAIL_REGEXP)) {
            successful = false;
            errors.put("email", true);
        }

        if (fac != null && Utility.containsInvalidMarkupCharacter(fac)) {
            successful = false;
            errors.put("fac", true);
        }

        if (position != null && Utility.containsInvalidMarkupCharacter(position)) {
            successful = false;
            errors.put("position", true);
        }

        if (successful) {
            User user;
            try {
                user = new UserBuilder().withFirstName(firstName.trim())
                        .withEmail(email)
                        .withLastName(lastName.trim()).withRole("representative")
                        .withPwd("Qwerty123")
                        .withDegree(degree).inInstitution(fac != null ? fac.trim() : null)
                        .inPosition(position != null ? position.trim() : null)
                        .build();
            } catch (ModelException e) {
                LOG.error("Could not register new representative with email {}.", email);
                session.setAttribute("popups", new String[]{"Hiba történt, próbáld újra."});
                resp.sendRedirect(req.getContextPath() + "/registerrepresentative");
                return;
            }

            try {
                uService.register(user);
                LOG.info("New representative successfully registered with email {}.", email);
                session.setAttribute("popups", new String[]{"Szekciófelelős sikeresen regisztrálva."});
                resp.sendRedirect(req.getContextPath() + "/profile");
            } catch (ServiceException e) {
                LOG.error("Could not register new representative with email {}.", email);
                session.setAttribute("popups", new String[]{"Hiba történt, próbáld újra."});
                resp.sendRedirect(req.getContextPath() + "/registerrepresentative");
            }
        } else {
            LOG.warn("Input errors while creating new representative.");
            session.setAttribute("errors", errors);
            resp.sendRedirect(req.getContextPath() + "/registerrepresentative");
        }
    }

}
