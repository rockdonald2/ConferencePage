package edu.conference.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class ModelFactory {

    private static Logger LOG = LoggerFactory.getLogger(ModelFactory.class);

    public static synchronized ConcurrentHashMap<String, Object> createModel(HttpServletRequest req) {
        HttpSession session = req.getSession();
        boolean isLogged = !Objects.isNull(session.getAttribute("email"));
        ConcurrentHashMap<String, Object> model = new ConcurrentHashMap<>();
        model.put("logged", isLogged);

        return model;
    }

}
