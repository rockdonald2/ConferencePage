package edu.conference.utils;

import edu.conference.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static edu.conference.utils.Utility.*;

public final class ModelFactory {

    public static synchronized ConcurrentHashMap<String, Object> createModel(HttpServletRequest req) {
        HttpSession session = req.getSession();
        boolean isLogged = !Objects.isNull(session.getAttribute(LOGGED));
        ConcurrentHashMap<String, Object> model = new ConcurrentHashMap<>();
        model.put("logged", isLogged);

        if (isLogged) {
            model.put("user", session.getAttribute(USER));
        } else {
            model.put("user", new User());
        }

        Object popups = session.getAttribute(POPUP);
        model.put(POPUP, popups == null ? Collections.emptyList() : popups);
        session.setAttribute(POPUP, Collections.emptyList());
        Object errors = session.getAttribute(ERROR);
        model.put(ERROR, errors == null ? Collections.emptyList() : errors);
        session.setAttribute(ERROR, Collections.emptyList());

        return model;
    }

}
