package model;

public class User extends BaseEntity {

    private String email;
    private String pwd;
    private String firstName;
    private String lastName;
    private Role role;
    private String institution;
    private String position;
    private String academicDegree;

    public User() {}

    public User(String email, String pwd, String firstName, String lastName, String role, String institution, String position, String academicDegree) {
        this.email = email;
        this.pwd = pwd;
        this.firstName = firstName;
        this.lastName = lastName;
        try {
            this.role = Role.get(role);
        } catch (ModelException e) {
            this.role = Role.GUEST;
        }
        this.institution = institution;
        this.position = position;
        this.academicDegree = academicDegree;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = Role.get(role);
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAcademicDegree() {
        return academicDegree;
    }

    public void setAcademicDegree(String academicDegree) {
        this.academicDegree = academicDegree;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", pwd='" + pwd + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                ", institution='" + institution + '\'' +
                ", position='" + position + '\'' +
                ", academicDegree='" + academicDegree + '\'' +
                '}';
    }

}
