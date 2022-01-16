package edu.conference.utils;

import org.apache.commons.lang3.StringUtils;

public final class Utility {

    public static String escapeMarkup(String textToBeEscaped) {
        return StringUtils.replaceEach(textToBeEscaped,
                new String[]{"<", ">"},
                new String[]{"&lt;", "&gt;"});
    }

    public static boolean containsInvalidMarkupCharacter(String text) {
        return text.contains("<") || text.contains(">");
    }

}
