package edu.conference.model.builders;

import edu.conference.model.Section;
import edu.conference.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SectionBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SectionBuilder.class);

    private String name;
    private String description;
    private User representative;

    public SectionBuilder() {}

    public SectionBuilder withName(String name) {
        this.name = name;

        return this;
    }

    public SectionBuilder withDescription(String description) {
        this.description = description;

        return this;
    }

    public SectionBuilder withRepresentative(User representative) {
        this.representative = representative;

        return this;
    }

    public Section build() {
        Section s = new Section(this.name, this.description, this.representative);

        LOG.info("Successfully builded section {}.", this.name);

        return s;
    }

}
