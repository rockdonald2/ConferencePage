package edu.conference.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.conference.model.Paper;
import edu.conference.model.User;
import edu.conference.service.PaperService;
import edu.conference.service.ServiceFactory;
import edu.conference.service.exception.ServiceException;
import edu.conference.utils.ModelFactory;
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

import static edu.conference.utils.Utility.alertRedirectUser;

@WebServlet({"/getPaper", "/getpaper"})
public class GetPaperServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(GetPaperServlet.class);

    private PaperService pService;

    @Override
    public void init() throws ServletException {
        super.init();
        pService = ServiceFactory.getInstance().getPaperService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);

        boolean isLogged = (boolean) model.get("logged");
        User curr = (User) model.get("user");

        if (!isLogged) {
            alertRedirectUser(req, resp, "Bejelentkezés szükséges.", 401, "/login");
            return;
        }

        long paperId = Long.parseLong(req.getParameter("paperId"));

        Paper paper;
        try {
            paper = pService.getById(paperId);
        } catch (ServiceException e) {
            LOG.error("Failed to get paper {}.", paperId);
            alertRedirectUser(req, resp, "Hiba történt, próbáld újra.", 500, "/index");
            return;
        }

        resp.setHeader("Content-Type", "application/json");
        new ObjectMapper().writeValue(resp.getWriter(), paper);
        LOG.info("User {} successfully accessed paper {}.", curr.getEmail(), paper.getId());
    }

}
