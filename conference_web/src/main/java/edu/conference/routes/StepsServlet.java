package edu.conference.routes;

import edu.conference.utils.ModelFactory;
import edu.conference.utils.TemplateFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/steps")
public class StepsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateFactory.getTemplate("steps").apply(ModelFactory.createModel(req), resp.getWriter());
    }

}
