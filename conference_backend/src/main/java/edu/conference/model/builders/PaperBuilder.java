package edu.conference.model.builders;

import edu.conference.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaperBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(PaperBuilder.class);

    private String title;
    private User presenter;
    private Section section;
    private String[] coAuthors;
    private String abstr;
    private String doc;
    private Status status;

    public PaperBuilder() {}

    public PaperBuilder withTitle(String title) {
        this.title = title;

        return this;
    }

    public PaperBuilder presents(User presenter) {
        this.presenter = presenter;

        return this;
    }

    public PaperBuilder inSection(Section section) {
        this.section = section;

        return this;
    }

    public PaperBuilder withCoAuthors(String[] coAuthors) {
        this.coAuthors = coAuthors;

        return this;
    }

    public PaperBuilder withAbstract(String abstr) {
        this.abstr = abstr;

        return this;
    }

    public PaperBuilder withDocument(String doc) {
        this.doc = doc;

        return this;
    }

    public PaperBuilder withStatus(String status) {
        try {
            this.status = Status.get(status);
        } catch (ModelException e) {
            LOG.error("Invalid paper status: {}. Setting to New.", status);
            this.status = Status.NEW;
        }

        return this;
    }

    public Paper build() {
        Paper p = new Paper(this.title, this.presenter, this.section, this.coAuthors, this.abstr, this.doc, this.status);

        LOG.info("Successfully builded paper {}.", this.title);

        return p;
    }

}
