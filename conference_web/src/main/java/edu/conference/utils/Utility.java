package edu.conference.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import static edu.conference.utils.Constants.PDF_CONTENT_TYPE;

public final class Utility {

    public static final String POPUP = "popups";
    public static final String ERROR = "errors";
    public static final String LOGGED = "logged";
    public static final String USER = "user";

    public static String escapeMarkup(String textToBeEscaped) {
        return StringUtils.replaceEach(textToBeEscaped,
                new String[]{"<", ">"},
                new String[]{"&lt;", "&gt;"});
    }

    public static boolean containsInvalidMarkupCharacter(String text) {
        return text.contains("<") || text.contains(">");
    }

    public static void alertRedirectUser(HttpServletRequest req, HttpServletResponse resp, String msg, int statusCode, String redirectRoute) throws IOException {
        if (msg != null) {
            req.getSession().setAttribute(POPUP, new String[]{ msg });
        }

        if (redirectRoute != null) {
            resp.sendRedirect(req.getContextPath() + redirectRoute);
        }

        if (statusCode != -1) {
            resp.setStatus(statusCode);
        }
    }

    public static boolean isPdfFile(Part file) {
        return file.getContentType().equals(PDF_CONTENT_TYPE);
    }

    public static boolean isEmptyFile(Part file) {
        return file == null || file.getSize() == 0;
    }

}
