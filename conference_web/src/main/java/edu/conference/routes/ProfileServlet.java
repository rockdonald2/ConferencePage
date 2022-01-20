package edu.conference.routes;

import edu.conference.model.*;
import edu.conference.service.PaperService;
import edu.conference.service.SectionService;
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
import java.util.List;
import java.util.Map;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileServlet.class);
    private static final int PAGE_SIZE = 3;

    private UserService uService;
    private PaperService pService;
    private SectionService sService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServiceFactory factory = ServiceFactory.getInstance();
        uService = factory.getUserService();
        pService = factory.getPaperService();
        sService = factory.getSectionService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> model = ModelFactory.createModel(req);

        User curr = (User) model.get("user");
        boolean todoStatusVal = true;
        List<Paper> papers = pService.getAll();
        if (curr.getRole().equals(Role.PRESENTER)) {
            todoStatusVal = uService.arePapersUploaded(curr);
            papers = pService.getAllForPresenter(curr.getEmail());

            List<Section> currentSections = new ArrayList<>();
            papers.forEach(paper -> currentSections.add(paper.getSection()));

            List<Section> allSections = sService.getAll();

            model.put("missingSectionsForUser", allSections.stream().filter(section -> currentSections.stream().noneMatch(currSection -> currSection.getName().equals(section.getName())))
                    .toArray());
        } else if (curr.getRole().equals(Role.GUEST)) {
            papers = papers.stream().filter(paper -> paper.getStatus() != Status.NEW)
                    .toList();
        }

        if (!curr.getRole().equals(Role.PRESENTER)) {
            String currPage = req.getParameter("page");

            int page = currPage == null ? 1 : Integer.parseInt(currPage);
            int maxPages = (int) Math.ceil((double) papers.size() / PAGE_SIZE);

            boolean onFirstPage = page == 1;
            boolean onLastPage = page == maxPages;

            if (page > maxPages) {
                resp.setStatus(400);
                resp.sendRedirect(req.getContextPath() + "/profile");
                return;
            }

            papers = papers.stream().skip((long) (page - 1) * PAGE_SIZE)
                    .limit(PAGE_SIZE).toList();

            model.put("currPage", page);
            model.put("onFirstPage", onFirstPage);
            model.put("onLastPage", onLastPage);
        }

        model.put("statuses", Status.class.getEnumConstants());
        model.put("todoStatus", todoStatusVal);
        model.put("papers", papers);

        TemplateFactory.getTemplate("profile").apply(model, resp.getWriter());
    }

}
