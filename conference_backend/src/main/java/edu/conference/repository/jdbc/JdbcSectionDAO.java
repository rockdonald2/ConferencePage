package edu.conference.repository.jdbc;

import edu.conference.model.Section;
import edu.conference.repository.exception.RepositoryException;
import edu.conference.util.PropertyProvider;
import edu.conference.model.User;
import edu.conference.model.builders.SectionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.conference.repository.DAOFactory;
import edu.conference.repository.SectionDAO;
import edu.conference.repository.UserDAO;
import edu.conference.repository.jdbc.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JdbcSectionDAO implements SectionDAO {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcSectionDAO.class);
    private final ConnectionManager connManager;

    public JdbcSectionDAO() throws RepositoryException {
        this.connManager = ConnectionManager.getInstance();

        boolean createTables = PropertyProvider.getBoolProperty("db.create.tables");
        if (createTables) {
            LOG.info("Creating table for sections.");

            Connection c = null;
            try {
                c = connManager.getConnection();

                Statement stmt = c.createStatement();
                stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS `section` (
                          `SectionID` int AUTO_INCREMENT NOT NULL,
                          `UUID` varchar(36) NOT NULL UNIQUE,
                          `Name` varchar(128) NOT NULL UNIQUE,
                          `Description` longtext NOT NULL,
                          `Email` varchar(128) NOT NULL,
                          PRIMARY KEY (SectionID),
                          CONSTRAINT FK_RESPONSABLE FOREIGN KEY (Email) REFERENCES conferenceuser (Email)) ENGINE=InnoDB;""");

                LOG.info("Successfully created table for sections.");
            } catch (SQLException e) {
                LOG.error("Failed to create table for sections.", e);
                throw new RepositoryException("Failed to create table for sections.");
            } finally {
                if (!Objects.isNull(c)) {
                    connManager.returnConnection(c);
                }
            }
        }
    }

    @Override
    public Section create(Section section) {
        Connection c = null;

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("INSERT INTO section (UUID, Name, Description, Email) VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, section.getUuid());
            stmt.setString(2, section.getName());
            stmt.setString(3, section.getDescription());
            stmt.setString(4, section.getRepresentative().getEmail());

            stmt.execute();

            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();

            section.setId(rs.getLong("SectionID"));

            LOG.info("Successfully created section {}.", section.getId());
            return section;
        } catch (SQLException e) {
            LOG.error("Failed to insert new section.", e);
            throw new RepositoryException("Failed to insert new section.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public void update(Section section) {
        Connection c = null;

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("UPDATE section SET Name = ?, Description = ?, Email = ? WHERE SectionID = ?;");

            stmt.setString(1, section.getName());
            stmt.setString(2, section.getDescription());
            stmt.setString(3, section.getRepresentative().getEmail());
            stmt.setLong(4, section.getId());

            stmt.executeUpdate();

            if (stmt.getUpdateCount() == 0) {
                LOG.error("Non-existing section with id {}.", section.getId());
                throw new RepositoryException("Non-existing section.");
            }

            LOG.info("Successfully updated section {}.", section.getId());
        } catch (SQLException e) {
            LOG.error("Failed to update section {}.", section.getId(), e);
            throw new RepositoryException("Failed to update section.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public void delete(Long id) {
        Connection c = null;

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("DELETE FROM section WHERE SectionID = ?;");

            stmt.setLong(1, id);

            stmt.execute();

            if (stmt.getUpdateCount() == 0) {
                LOG.error("Non-existing section with id {}.", id);
                throw new RepositoryException("Non-existing section.");
            }

            LOG.info("Successfully deleted section {}.", id);
        } catch (SQLException e) {
            LOG.error("Failed to delete section {}.", id, e);
            throw new RepositoryException("Failed to delete section.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public Section getById(Long id) {
        Connection c = null;
        Section s;

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("SELECT * FROM section WHERE SectionID = ?;");

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                LOG.warn("Non-existing section with id {}.", id);
                return null;
            }

            SectionBuilder sectionBuilder = new SectionBuilder();
            DAOFactory factory = DAOFactory.getInstance();
            UserDAO userDao = factory.getUserDAO();

            User u = userDao.getByEmail(rs.getString("Email"));

            if (Objects.isNull(u)) {
                LOG.warn("Non-existing user {} while querying section {}.", rs.getString("Email"), rs.getString("Name"));
                return null;
            }

            s = sectionBuilder.withName(rs.getString("Name")).withDescription(rs.getString("Description")).withRepresentative(u).build();
            s.setId(rs.getLong("SectionID"));
            s.setUuid(rs.getString("UUID"));

            LOG.info("Successfully retrieved section {}.", s.getId());
            return s;
        } catch (SQLException e) {
            LOG.error("Failed to query seciton {}.", id, e);
            throw new RepositoryException("Failed to query section.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public Section getByName(String name) {
        Connection c = null;
        Section s;

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("SELECT * FROM section WHERE Name = ?;");

            stmt.setString(1, name);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                LOG.warn("Non-existing section with name {}.", name);
                return null;
            }

            SectionBuilder sectionBuilder = new SectionBuilder();
            DAOFactory factory = DAOFactory.getInstance();
            UserDAO userDao = factory.getUserDAO();

            User u = userDao.getByEmail(rs.getString("Email"));

            if (Objects.isNull(u)) {
                LOG.warn("Non-existing user {} while querying section {}.", rs.getString("Email"), rs.getString("Name"));
                return null;
            }

            s = sectionBuilder.withName(rs.getString("Name")).withDescription(rs.getString("Description")).withRepresentative(u).build();
            s.setId(rs.getLong("SectionID"));
            s.setUuid(rs.getString("UUID"));

            LOG.info("Successfully retrieved section {}.", s.getId());
            return s;
        } catch (SQLException e) {
            LOG.error("Failed to query section {}.", name, e);
            throw new RepositoryException("Failed to query section.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public List<Section> getAll() {
        Connection c = null;
        List<Section> sections = new ArrayList<>();

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("SELECT * FROM section;");

            ResultSet rs = stmt.executeQuery();

            SectionBuilder sectionBuilder = new SectionBuilder();
            DAOFactory factory = DAOFactory.getInstance();
            UserDAO userDao = factory.getUserDAO();
            User u;
            Section s;

            while (rs.next()) {
                u = userDao.getByEmail(rs.getString("Email"));

                if (Objects.isNull(u)) {
                    LOG.warn("Non-existing user {} while querying section {}.", rs.getString("Email"), rs.getString("Name"));
                    continue;
                }

                s = sectionBuilder.withName(rs.getString("Name")).withDescription(rs.getString("Description")).withRepresentative(u).build();
                s.setId(rs.getLong("SectionID"));
                s.setUuid(rs.getString("UUID"));

                LOG.info("Successfully retrieved section {}.", s.getId());
                sections.add(s);
            }

            return sections;
        } catch (SQLException e) {
            LOG.error("Failed to query all sections.", e);
            throw new RepositoryException("Failed to query all sections.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public List<Section> getAllForRepresentative(String email) {
        Connection c = null;
        List<Section> sections = new ArrayList<>();

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("SELECT * FROM section WHERE Email = ?;");

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            SectionBuilder sectionBuilder = new SectionBuilder();
            DAOFactory factory = DAOFactory.getInstance();
            UserDAO userDao = factory.getUserDAO();
            User u;
            Section s;

            u = userDao.getByEmail(email);

            if (Objects.isNull(u)) {
                LOG.warn("Non-existing user {} while querying section {}.", rs.getString("Email"), rs.getString("Name"));
                return Collections.emptyList();
            }

            while (rs.next()) {
                s = sectionBuilder.withName(rs.getString("Name")).withDescription(rs.getString("Description")).withRepresentative(u).build();
                s.setId(rs.getLong("SectionID"));
                s.setUuid(rs.getString("UUID"));

                LOG.info("Successfully retrieved section {}.", s.getId());
                sections.add(s);
            }

            return sections;
        } catch (SQLException e) {
            LOG.error("Failed to query all sections for user {}.", email, e);
            throw new RepositoryException("Failed to query all sections.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

}
