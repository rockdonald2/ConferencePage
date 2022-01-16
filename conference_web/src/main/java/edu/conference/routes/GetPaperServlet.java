package edu.conference.routes;

import com.cedarsoftware.util.io.JsonWriter;
import edu.conference.model.Paper;
import edu.conference.model.Role;
import edu.conference.model.User;
import edu.conference.service.PaperService;
import edu.conference.service.ServiceFactory;
import edu.conference.utils.ModelFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

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
            resp.setStatus(401);
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (curr.getRole().equals(Role.GUEST)) {
            resp.setStatus(403);
            resp.sendRedirect(req.getContextPath() + "/index");
            return;
        }

        long paperId = Long.parseLong(req.getParameter("paperId"));
        Paper paper = pService.getById(paperId);
        String jsonResp = JsonWriter.objectToJson(paper);

        if (curr.getRole().equals(Role.ADMIN)) {
            JsonWriter.writeJsonUtf8String(jsonResp, resp.getWriter());
        } else if (curr.getRole().equals(Role.REPRESENTATIVE) && paper.getSection().getRepresentative().getEmail().equals(curr.getEmail())) {
            JsonWriter.writeJsonUtf8String(jsonResp, resp.getWriter());
        } else if (curr.getRole().equals(Role.PRESENTER) && paper.getPresenter().getEmail().equals(curr.getEmail())) {
            JsonWriter.writeJsonUtf8String(jsonResp, resp.getWriter());
        }
    }

}
