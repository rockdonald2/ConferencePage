package edu.conference.routes;

import edu.conference.utils.ModelFactory;
import edu.conference.utils.TemplateFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/404")
public class NotFoundServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateFactory.getTemplate("404").apply(ModelFactory.createModel(req), resp.getWriter());
    }

}
