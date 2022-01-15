package edu.conference.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Objects;

@WebFilter({"/login", "/registration", "/forgottenPassword"})
public class LoggedInFilter extends HttpFilter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpResp = (HttpServletResponse) res;

        HttpSession session = httpReq.getSession();
        boolean isLogged = !Objects.isNull(session.getAttribute("logged"));

        if (isLogged) {
            httpResp.sendRedirect(httpReq.getContextPath() + "/index");
        } else {
            chain.doFilter(req, res);
        }
    }

}
