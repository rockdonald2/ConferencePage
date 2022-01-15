package edu.conference.routes;

import edu.conference.repository.DAOFactory;
import edu.conference.repository.SectionDAO;
import edu.conference.utils.ModelFactory;
import edu.conference.utils.TemplateFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/conditions")
public class ConditionsServlet extends HttpServlet {

    private SectionDAO sDao;

    @Override
    public void init() throws ServletException {
        super.init();
        sDao = DAOFactory.getInstance().getSectionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);
        var sections = sDao.getAll();
        model.put("sections", sections);

        TemplateFactory.getTemplate("conditions").apply(model, resp.getWriter());
    }

}
