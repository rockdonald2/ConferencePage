package edu.conference.routes;

import edu.conference.model.Section;
import edu.conference.model.User;
import edu.conference.service.SectionService;
import edu.conference.service.ServiceFactory;
import edu.conference.service.exception.ServiceException;
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
import java.util.List;
import java.util.Map;

import static edu.conference.utils.Utility.alertRedirectUser;

@WebServlet("/conditions")
public class ConditionsServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ConditionsServlet.class);

    private SectionService sService;

    @Override
    public void init() throws ServletException {
        super.init();
        sService = ServiceFactory.getInstance().getSectionService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);

        List<Section> sections;
        try {
            sections = sService.getAll();
        } catch (ServiceException e) {
            LOG.error("Failed to get sections.");
            alertRedirectUser(req, resp, "Hiba történt, próbáld újra.", 500, "/index");
            return;
        }

        model.put("sections", sections);
        TemplateFactory.getTemplate("conditions").apply(model, resp.getWriter());
    }

}
