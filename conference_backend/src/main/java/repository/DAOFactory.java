package repository;

import repository.jdbc.JdbcDAOFactory;

public abstract class DAOFactory {

    public abstract UserDAO getUserDAO();

    public static DAOFactory getInstance() {
        return new JdbcDAOFactory();
    }

}
