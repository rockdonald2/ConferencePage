package repository.jdbc;

import repository.DAOFactory;
import repository.UserDAO;

public class JdbcDAOFactory extends DAOFactory {

    @Override
    public UserDAO getUserDAO() {
        return new JdbcUserDAO();
    }

}
