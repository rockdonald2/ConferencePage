package edu.conference.model;

import java.util.Arrays;

public class Paper extends BaseEntity {

    private String title;
    private User presenter;
    private Section section;
    private String[] coAuthors;
    private String abstr;
    private String doc;
    private Status status;

    public Paper() {}

    public Paper(String title, User presenter, Section section, String[] coAuthors, String abstr, String doc, Status status) {
        this.title = title;
        this.presenter = presenter;
        this.section = section;
        this.coAuthors = coAuthors;
        this.abstr = abstr;
        this.doc = doc;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getPresenter() {
        return presenter;
    }

    public void setPresenter(User presenter) {
        this.presenter = presenter;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public String[] getCoAuthors() {
        return coAuthors;
    }

    public void setCoAuthors(String[] coAuthors) {
        this.coAuthors = coAuthors;
    }

    public String getAbstr() {
        return abstr;
    }

    public void setAbstr(String abstr) {
        this.abstr = abstr;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Paper{" +
                "title='" + title + '\'' +
                ", presenter=" + presenter +
                ", section=" + section +
                ", coAuthors=" + Arrays.toString(coAuthors) +
                ", abstr='" + abstr + '\'' +
                ", doc='" + doc + '\'' +
                ", status=" + status +
                '}';
    }

}
