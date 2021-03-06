package edu.conference.repository.jdbc;

import edu.conference.model.Paper;
import edu.conference.model.Section;
import edu.conference.model.builders.PaperBuilder;
import edu.conference.repository.DAOFactory;
import edu.conference.repository.PaperDAO;
import edu.conference.repository.SectionDAO;
import edu.conference.repository.UserDAO;
import edu.conference.repository.exception.RepositoryException;
import edu.conference.util.PropertyProvider;
import edu.conference.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.conference.repository.jdbc.utils.ConnectionManager;

import java.sql.*;
import java.util.*;

public class JdbcPaperDAO implements PaperDAO {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcPaperDAO.class);
    private final ConnectionManager connManager;

    public JdbcPaperDAO() {
        connManager = ConnectionManager.getInstance();

        boolean createTables = PropertyProvider.getBoolProperty("db.create.tables");
        if (createTables) {
            LOG.info("Creating table for papers.");

            Connection c = null;
            try {
                c = connManager.getConnection();

                Statement stmt = c.createStatement();
                stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS `paper` (
                          `PaperID` int AUTO_INCREMENT NOT NULL,
                          `UUID` varchar(36) NOT NULL UNIQUE,
                          `Document` varchar(512) DEFAULT NULL UNIQUE,
                          `Status` enum('New','Accepted','Confirmed','Rejected') NOT NULL,
                          `Abstract` mediumtext NOT NULL,
                          `Title` varchar(512) NOT NULL,
                          `CoAuthors` text NOT NULL,
                          `Email` varchar(128) NOT NULL,
                          `SectionID` int(11) NOT NULL,
                          PRIMARY KEY (PaperID),
                          CONSTRAINT FK_BELONGSUSER FOREIGN KEY (Email) REFERENCES conferenceuser (Email),
                          CONSTRAINT FK_BELONGSSECTION FOREIGN KEY (SectionID) REFERENCES section (SectionID)
                        ) ENGINE=InnoDB;""");

                LOG.info("Successfully created table for papers.");
            } catch (SQLException e) {
                LOG.error("Failed to create table for papers.", e);
                throw new RepositoryException("Failed to create table for papers.");
            } finally {
                if (!Objects.isNull(c)) {
                    connManager.returnConnection(c);
                }
            }
        }
    }

    @Override
    public Paper create(Paper paper) throws RepositoryException {
        Connection c = null;

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("INSERT INTO paper (UUID, Document, Status, Abstract, Title, CoAuthors, Email, SectionID) VALUES (?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, paper.getUuid());
            stmt.setString(2, paper.getDoc());
            stmt.setString(3, paper.getStatus().toString());
            stmt.setString(4, paper.getAbstr());
            stmt.setString(5, paper.getTitle());
            stmt.setString(6, paper.getCoAuthors() != null ? String.join(",", paper.getCoAuthors()) : null);
            stmt.setString(7, paper.getPresenter().getEmail());
            stmt.setLong(8, paper.getSection().getId());

            stmt.execute();

            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();

            paper.setId(rs.getLong("PaperID"));

            LOG.info("Successfully created paper {}.", paper.getId());
            return paper;
        } catch (SQLException e) {
            LOG.error("Failed to insert new paper.", e);
            throw new RepositoryException("Failed to insert new paper.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public void update(Paper paper) {
        Connection c = null;

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("UPDATE paper SET Document = ?, Status = ?, Abstract = ?, Title = ?, CoAuthors = ? WHERE PaperID = ?;");

            stmt.setString(1, paper.getDoc());
            stmt.setString(2, paper.getStatus().toString());
            stmt.setString(3, paper.getAbstr());
            stmt.setString(4, paper.getTitle());
            stmt.setString(5, paper.getCoAuthors() != null ? String.join(", ", paper.getCoAuthors()) : null);
            stmt.setLong(6, paper.getId());

            stmt.executeUpdate();

            if (stmt.getUpdateCount() == 0) {
                LOG.error("Non-existing paper with id {}.", paper.getId());
                throw new RepositoryException("Non-existing paper.");
            }

            LOG.info("Successfully updated paper {}.", paper.getId());
        } catch (SQLException e) {
            LOG.error("Failed to update paper {}.", paper.getId(), e);
            throw new RepositoryException("Failed to update paper.");
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

            PreparedStatement stmt = c.prepareStatement("DELETE FROM paper WHERE PaperID = ?;");

            stmt.setLong(1, id);

            stmt.execute();

            if (stmt.getUpdateCount() == 0) {
                LOG.error("Non-existing paper with id {}.", id);
                throw new RepositoryException("Non-existing paper.");
            }

            LOG.info("Successfully deleted paper {}.", id);
        } catch (SQLException e) {
            LOG.error("Failed to delete paper {}.", id, e);
            throw new RepositoryException("Failed to update paper.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public Paper getById(Long id) {
        Connection c = null;
        Paper p;

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("SELECT * FROM paper WHERE PaperID = ?;");

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                LOG.warn("Non-existing paper with id {}.", id);
                return null;
            }

            PaperBuilder builder = new PaperBuilder();
            DAOFactory factory = DAOFactory.getInstance();
            SectionDAO sectionDao = factory.getSectionDAO();
            UserDAO userDao = factory.getUserDAO();

            Section s = sectionDao.getById(rs.getLong("SectionID"));

            if (Objects.isNull(s)) {
                LOG.warn("Non-existing section {} when querying paper {}.", rs.getLong("SectionID"), id);
                return null;
            }

            User u = userDao.getByEmail(rs.getString("Email"));

            if (Objects.isNull(u)) {
                LOG.warn("Non-existing user {} when querying paper {}.", rs.getLong("Email"), id);
                return null;
            }

            p = builder.withTitle(rs.getString("Title")).withAbstract(rs.getString("Abstract")).withDocument(rs.getString("Document")).withStatus(rs.getString("Status"))
                    .withCoAuthors(rs.getString("CoAuthors") != null ? rs.getString("CoAuthors").split(", ") : null)
                    .inSection(s).presents(u).build();
            p.setId(rs.getLong("PaperID"));
            p.setUuid(rs.getString("UUID"));

            LOG.info("Successfully retrieved paper {}.", p.getId());
            return p;
        } catch (SQLException e) {
            LOG.error("Failed to get paper {}.", id, e);
            throw new RepositoryException("Failed to get paper.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public Paper getByPath(String path) {
        Connection c = null;
        Paper p;

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("SELECT * FROM paper WHERE Document = ?;");

            stmt.setString(1, path);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                LOG.warn("Non-existing paper with path {}.", path);
                return null;
            }

            PaperBuilder builder = new PaperBuilder();
            DAOFactory factory = DAOFactory.getInstance();
            SectionDAO sectionDao = factory.getSectionDAO();
            UserDAO userDao = factory.getUserDAO();

            Section s = sectionDao.getById(rs.getLong("SectionID"));

            if (Objects.isNull(s)) {
                LOG.warn("Non-existing section {} when querying paper {}.", rs.getLong("SectionID"), path);
                return null;
            }

            User u = userDao.getByEmail(rs.getString("Email"));

            if (Objects.isNull(u)) {
                LOG.warn("Non-existing user {} when querying paper {}.", rs.getLong("Email"), path);
                return null;
            }

            p = builder.withTitle(rs.getString("Title")).withAbstract(rs.getString("Abstract")).withDocument(rs.getString("Document")).withStatus(rs.getString("Status"))
                    .withCoAuthors(rs.getString("CoAuthors") != null ? rs.getString("CoAuthors").split(", ") : null)
                    .inSection(s).presents(u).build();
            p.setId(rs.getLong("PaperID"));
            p.setUuid(rs.getString("UUID"));

            LOG.info("Successfully retrieved paper {}.", p.getId());
            return p;
        } catch (SQLException e) {
            LOG.error("Failed to get paper {}.", path, e);
            throw new RepositoryException("Failed to get paper.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public List<Paper> getAll() {
        Connection c = null;
        List<Paper> papers = new ArrayList<>();

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("SELECT * FROM paper;");

            ResultSet rs = stmt.executeQuery();

            PaperBuilder builder = new PaperBuilder();
            DAOFactory factory = DAOFactory.getInstance();
            SectionDAO sectionDao = factory.getSectionDAO();
            UserDAO userDao = factory.getUserDAO();
            Section s;
            User u;
            Paper p;

            while (rs.next()) {
                s = sectionDao.getById(rs.getLong("SectionID"));

                if (Objects.isNull(s)) {
                    LOG.warn("Non-existing section {} when querying paper {}.", rs.getLong("SectionID"), rs.getLong("PaperID"));
                    continue;
                }

                u = userDao.getByEmail(rs.getString("Email"));

                if (Objects.isNull(u)) {
                    LOG.warn("Non-existing user {} when querying paper {}.", rs.getLong("Email"), rs.getLong("PaperID"));
                    continue;
                }

                p = builder.withTitle(rs.getString("Title")).withAbstract(rs.getString("Abstract")).withDocument(rs.getString("Document")).withStatus(rs.getString("Status"))
                        .withCoAuthors(rs.getString("CoAuthors") != null ? rs.getString("CoAuthors").split(", ") : null)
                        .inSection(s).presents(u).build();
                p.setId(rs.getLong("PaperID"));
                p.setUuid(rs.getString("UUID"));

                LOG.info("Successfully retrieved paper {}.", p.getId());
                papers.add(p);
            }

            return papers;
        } catch (SQLException e) {
            LOG.error("Failed to query papers.", e);
            throw new RepositoryException("Failed to query papers.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public List<Paper> getAllForPresenter(String email) {
        Connection c = null;
        List<Paper> papers = new ArrayList<>();

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("SELECT * FROM paper WHERE Email = ?;");

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            PaperBuilder builder = new PaperBuilder();
            DAOFactory factory = DAOFactory.getInstance();
            SectionDAO sectionDao = factory.getSectionDAO();
            UserDAO userDao = factory.getUserDAO();
            Section s;
            User u;
            Paper p;

            u = userDao.getByEmail(email);

            if (Objects.isNull(u)) {
                LOG.warn("Non-existing user {} when querying paper {}.", rs.getLong("Email"), rs.getLong("PaperID"));
                return Collections.emptyList();
            }

            while (rs.next()) {
                s = sectionDao.getById(rs.getLong("SectionID"));

                if (Objects.isNull(s)) {
                    LOG.warn("Non-existing section {} when querying paper {}.", rs.getLong("SectionID"), rs.getLong("PaperID"));
                    continue;
                }

                p = builder.withTitle(rs.getString("Title")).withAbstract(rs.getString("Abstract")).withDocument(rs.getString("Document")).withStatus(rs.getString("Status"))
                        .withCoAuthors(rs.getString("CoAuthors") != null ? rs.getString("CoAuthors").split(", ") : null)
                        .inSection(s).presents(u).build();
                p.setId(rs.getLong("PaperID"));
                p.setUuid(rs.getString("UUID"));

                LOG.info("Successfully retrieved paper {}.", p.getId());
                papers.add(p);
            }

            return papers;
        } catch (SQLException e) {
            LOG.error("Failed to query papers for user {}.", email, e);
            throw new RepositoryException("Failed to query papers.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public List<Paper> getAllForSection(Long id) {
        Connection c = null;
        List<Paper> papers = new ArrayList<>();

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("SELECT * FROM paper WHERE SectionID = ?;");

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            PaperBuilder builder = new PaperBuilder();
            DAOFactory factory = DAOFactory.getInstance();
            SectionDAO sectionDao = factory.getSectionDAO();
            UserDAO userDao = factory.getUserDAO();
            Section s;
            User u;
            Paper p;

            s = sectionDao.getById(id);

            if (Objects.isNull(s)) {
                LOG.warn("Non-existing section {} when querying paper {}.", rs.getLong("SectionID"), rs.getLong("PaperID"));
                return Collections.emptyList();
            }

            while (rs.next()) {
                u = userDao.getByEmail(rs.getString("Email"));

                if (Objects.isNull(u)) {
                    LOG.warn("Non-existing user {} when querying paper {}.", rs.getLong("Email"), rs.getLong("PaperID"));
                    continue;
                }

                p = builder.withTitle(rs.getString("Title")).withAbstract(rs.getString("Abstract")).withDocument(rs.getString("Document")).withStatus(rs.getString("Status")).withCoAuthors(rs.getString("CoAuthors").split(", ")).inSection(s).presents(u).build();
                p.setId(rs.getLong("PaperID"));
                p.setUuid(rs.getString("UUID"));

                LOG.info("Successfully retrieved paper {}.", p.getId());
                papers.add(p);
            }

            return papers;
        } catch (SQLException e) {
            LOG.error("Failed to query papers for section {}.", id, e);
            throw new RepositoryException("Failed to query papers.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

}
