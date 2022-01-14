package repository.jdbc;

import repository.DAOFactory;
import repository.PaperDAO;
import repository.SectionDAO;
import repository.UserDAO;

public class JdbcDAOFactory extends DAOFactory {

    @Override
    public UserDAO getUserDAO() {
        return new JdbcUserDAO();
    }

    @Override
    public SectionDAO getSectionDAO() {
        return new JdbcSectionDAO();
    }

    @Override
    public PaperDAO getPaperDAO() {
        return new JdbcPaperDAO();
    }

}
