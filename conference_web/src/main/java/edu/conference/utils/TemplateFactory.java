package edu.conference.utils;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.beryx.hbs.Helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class TemplateFactory {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateFactory.class);

    // Handlebars sablonmotra
    private static Handlebars handlebars;

    public static synchronized Template getTemplate(String templateName) throws IOException {
        // lazy initialization
        if (handlebars == null) {
            // A sablonokat a classpath-ből töltjük be (src/main/resources),
            // ezen belül megjelölünk egy dedikált foldert nekik (templates)
            TemplateLoader loader = new ClassPathTemplateLoader();
            loader.setPrefix("/templates");
            loader.setSuffix(".hbs");
            handlebars = new Handlebars(loader);
            Helpers.register(handlebars);
            handlebars.registerHelper("math", new MathHelpers());
        }

        return handlebars.compile(templateName);
    }

}
