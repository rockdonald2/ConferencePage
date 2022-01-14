package repository;

import repository.jdbc.JdbcDAOFactory;

public abstract class DAOFactory {

    public abstract UserDAO getUserDAO();
    public abstract SectionDAO getSectionDAO();
    public abstract PaperDAO getPaperDAO();

    public static DAOFactory getInstance() {
        return new JdbcDAOFactory();
    }

}
