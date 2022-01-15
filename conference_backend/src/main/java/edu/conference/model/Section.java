package edu.conference.model;

public class Section extends BaseEntity {

    private String name;
    private String description;
    private User representative;

    public Section() {}

    public Section(String name, String description, User representative) {
        this.name = name;
        this.description = description;
        this.representative = representative;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getRepresentative() {
        return representative;
    }

    public void setRepresentative(User representative) {
        this.representative = representative;
    }

    @Override
    public String toString() {
        return "Section{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", representative=" + representative +
                '}';
    }

}
