package edu.conference.model.builders;

import edu.conference.model.ModelException;
import edu.conference.model.Role;
import edu.conference.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(UserBuilder.class);

    private String email;
    private String pwd;
    private String firstName;
    private String lastName;
    private Role role;
    private String institution;
    private String position;
    private String academicDegree;

    public UserBuilder() {}

    public UserBuilder withEmail(String email) {
        this.email = email;

        return this;
    }

    public UserBuilder withPwd(String pwd) {
        this.pwd = pwd;

        return this;
    }

    public UserBuilder withFirstName(String firstName) {
        this.firstName = firstName;

        return this;
    }

    public UserBuilder withLastName(String lastName) {
        this.lastName = lastName;

        return this;
    }

    public UserBuilder withRole(String role) {
        try {
            this.role = Role.get(role);
        } catch (ModelException e) {
            LOG.error("Invalid role: {}. Setting to Guest.", role);
            this.role = Role.GUEST;
        }

        return this;
    }

    public UserBuilder inInstitution(String institution) {
        this.institution = institution;

        return this;
    }

    public UserBuilder inPosition(String position) {
        this.position = position;

        return this;
    }

    public UserBuilder withDegree(String academicDegree) {
        this.academicDegree = academicDegree;

        return this;
    }

    public User build() {
        if (this.email == null || this.pwd == null) {
            throw new ModelException("Invalid user builder, missing email or password.");
        }

        User u = new User(this.email, this.pwd, this.firstName, this.lastName, this.role, this.institution, this.position, this.academicDegree);

        LOG.info("Successfully builded user {}.", this.email);

        return u;
    }

}
