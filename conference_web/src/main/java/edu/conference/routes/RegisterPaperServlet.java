package edu.conference.routes;

import edu.conference.model.Paper;
import edu.conference.model.Section;
import edu.conference.model.User;
import edu.conference.model.builders.PaperBuilder;
import edu.conference.service.PaperService;
import edu.conference.service.SectionService;
import edu.conference.service.ServiceFactory;
import edu.conference.utils.ModelFactory;
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
import java.util.Arrays;
import java.util.HashMap;
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
        ServiceFactory factory = ServiceFactory.getInstance();
        pService = factory.getPaperService();
        sService = factory.getSectionService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);

        Section section = sService.getByName(req.getParameter("new-section"));
        User user = (User) model.get("user");

        List<String> coAuthors = Arrays.stream(req.getParameter("new-coauthors").split(", "))
                .dropWhile(elem -> elem.equals(" ") || elem.equals("")).toList();
        String title = req.getParameter("new-title");
        String abstr = req.getParameter("new-abstract");

        boolean successful = true;
        HttpSession session = req.getSession();
        Map<String, Boolean> errors = new HashMap<>();

        if (Utility.containsInvalidMarkupCharacter(title)) {
            successful = false;
            errors.put("title", true);
        }

        if (Utility.containsInvalidMarkupCharacter(abstr)) {
            successful = false;
            errors.put("abstr", true);
        }

        if (successful) {
            Paper paper = new PaperBuilder().withTitle(title)
                    .withAbstract(abstr).withStatus("new")
                    .withCoAuthors(coAuthors.size() != 0 ? coAuthors.toArray(new String[] {}) : null)
                    .inSection(section)
                    .presents(user)
                    .build();

            pService.register(paper);
        } else {
            session.setAttribute("errors", errors);
        }

        resp.sendRedirect(req.getContextPath() + "/profile");
    }

}
