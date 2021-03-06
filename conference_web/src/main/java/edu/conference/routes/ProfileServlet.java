package edu.conference.routes;

import edu.conference.model.*;
import edu.conference.service.PaperService;
import edu.conference.service.SectionService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static edu.conference.utils.Utility.alertRedirectUser;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileServlet.class);
    private static final int PAGE_SIZE = 5;

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

        List<Paper> papers;
        try {
            papers = pService.getAll();
        } catch (ServiceException e) {
            LOG.error("Failed to load papers for profile page.");
            alertRedirectUser(req, resp, "Hiba történt, próbáld újra.", 500, "/index");
            return;
        }

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
            String currQuery = req.getParameter("query");

            String query = currQuery == null ? "" : currQuery;

            if (!query.equals("")) {
                papers = papers.stream().filter(paper -> {
                    User presenter = paper.getPresenter();

                    return (presenter.getLastName() + presenter.getFirstName()).contains(query) || paper.getTitle().contains(query);
                }).toList();
            }

            int page = currPage == null ? 1 : Integer.parseInt(currPage);
            int maxPages = (int) Math.ceil((double) papers.size() / PAGE_SIZE);

            // ha nincs találat az adott keresésre/nincs feltöltött dolgozat
            if (maxPages == 0) maxPages = 1;

            boolean onFirstPage = page == 1;
            boolean onLastPage = page == maxPages;

            if (page > maxPages) {
                LOG.error("Non-existing page tried to be queried by user {}.", curr.getEmail());
                alertRedirectUser(req, resp, "Nem létező oldalszám.", 404, "/profile");
                return;
            }

            papers = papers.stream().skip((long) (page - 1) * PAGE_SIZE)
                    .limit(PAGE_SIZE).toList();

            model.put("lastQuery", query);
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
