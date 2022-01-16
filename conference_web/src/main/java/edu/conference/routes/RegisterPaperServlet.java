package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.model.Section;
import edu.conference.model.User;
import edu.conference.model.builders.PaperBuilder;
import edu.conference.service.PaperService;
import edu.conference.service.SectionService;
import edu.conference.service.ServiceFactory;
import edu.conference.service.UserService;
import edu.conference.utils.ModelFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@WebServlet({"/registerpaper", "/registerPaper"})
public class RegisterPaperServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterPaperServlet.class);

    private PaperService pService;
    private SectionService sService;

    @Override
    public void init() throws ServletException {
        super.init();
        pService = ServiceFactory.getInstance().getPaperService();
        sService = ServiceFactory.getInstance().getSectionService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);

        Section section = sService.getByName(req.getParameter("new-section"));
        User user = (User) model.get("user");

        List<String> coAuthors = Arrays.stream(req.getParameter("new-coauthors").split(", "))
                .dropWhile(elem -> elem.equals(" ") || elem.equals("")).toList();

        Paper paper = new PaperBuilder().withTitle(req.getParameter("new-title"))
                .withAbstract(req.getParameter("new-abstract")).withStatus("new")
                .withCoAuthors(coAuthors.size() != 0 ? coAuthors.toArray(new String[] {}) : null)
                .inSection(section)
                .presents(user)
                .build();

        pService.register(paper);

        resp.sendRedirect(req.getContextPath() + "/profile");
    }

}
