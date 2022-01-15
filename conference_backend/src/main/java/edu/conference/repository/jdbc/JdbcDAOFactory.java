package edu.conference.repository.jdbc;

import edu.conference.repository.PaperDAO;
import edu.conference.repository.SectionDAO;
import edu.conference.repository.DAOFactory;
import edu.conference.repository.UserDAO;

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
