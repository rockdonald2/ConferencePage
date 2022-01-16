package edu.conference.utils;

import edu.conference.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class ModelFactory {

    public static synchronized ConcurrentHashMap<String, Object> createModel(HttpServletRequest req) {
        HttpSession session = req.getSession();
        boolean isLogged = !Objects.isNull(session.getAttribute("logged"));
        ConcurrentHashMap<String, Object> model = new ConcurrentHashMap<>();
        model.put("logged", isLogged);

        if (isLogged) {
            model.put("user", session.getAttribute("user"));
        } else {
            model.put("user", new User());
        }

        Object popups = session.getAttribute("popups");
        model.put("popups", popups == null ? Collections.emptyList() : popups);
        session.setAttribute("popups", Collections.emptyList());
        Object errors = session.getAttribute("errors");
        model.put("errors", errors == null ? Collections.emptyList() : errors);
        session.setAttribute("errors", Collections.emptyList());

        return model;
    }

}
