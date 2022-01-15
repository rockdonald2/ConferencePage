package edu.conference.repository.jdbc;

import edu.conference.model.Role;
import edu.conference.model.User;
import edu.conference.repository.exception.RepositoryException;
import edu.conference.util.PropertyProvider;
import edu.conference.model.builders.UserBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.conference.repository.UserDAO;
import edu.conference.repository.jdbc.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JdbcUserDAO implements UserDAO {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcUserDAO.class);

    private final ConnectionManager connManager;

    public JdbcUserDAO() throws RepositoryException {
        connManager = ConnectionManager.getInstance();

        boolean createTables = PropertyProvider.getBoolProperty("db.create.tables");
        if (createTables) {
            LOG.info("Creating table for users.");

            Connection c = null;
            try {
                c = connManager.getConnection();
                Statement stmt = c.createStatement();
                stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS `conferenceuser` ( `UserID` int AUTO_INCREMENT NOT NULL, `UUID` varchar(36) UNIQUE NOT NULL, `Email` varchar(128) UNIQUE NOT NULL, `FirstName` varchar(256) NOT NULL, `LastName` varchar(256) NOT NULL, `Pwd` char(40) NOT NULL, `Role` enum('GUEST','ADMIN','PRESENTER','REPRESENTATIVE') NOT NULL, `Institution` varchar(128) DEFAULT NULL, `Position` varchar(128) DEFAULT NULL, `AcademicDegree` varchar(3) NOT NULL, PRIMARY KEY (UserID)) ENGINE=InnoDB;""");

                LOG.info("Successfully created table for users.");
            } catch (SQLException e) {
                LOG.error("Failed to create table for users.", e);
                throw new RepositoryException("Failed to create table for users.");
            } finally {
                if (!Objects.isNull(c)) {
                    connManager.returnConnection(c);
                }
            }
        }
    }

    @Override
    public User create(User user) throws RepositoryException {
        Connection c = null;

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("INSERT INTO conferenceuser (UUID, Email, FirstName, LastName, Pwd, Role, Institution, Position, AcademicDegree) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, user.getUuid());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getPwd());
            stmt.setString(6, user.getRole().toString());
            stmt.setString(7, user.getInstitution());
            stmt.setString(8, user.getPosition());
            stmt.setString(9, user.getAcademicDegree());

            stmt.execute();

            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();

            user.setId(rs.getLong("UserID"));

            LOG.info("Successfully created user {}.", user.getId());
            return user;
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to insert new user.", e);
            throw new RepositoryException("Failed to insert new user.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public void update(User user) throws RepositoryException {
        Connection c = null;

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("UPDATE conferenceuser " +
                    "SET Email = ?, FirstName = ?, LastName = ?, Pwd = ?, Role = ?, Institution = ?, Position = ?, AcademicDegree = ? WHERE UserID = ?;");

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getPwd());
            stmt.setString(5, user.getRole().toString());
            stmt.setString(6, user.getInstitution());
            stmt.setString(7, user.getPosition());
            stmt.setString(8, user.getAcademicDegree());
            stmt.setLong(9, user.getId());

            stmt.executeUpdate();

            LOG.info("Successfully updated user {}.", user.getId());
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to update user {}.", user.getEmail(), e);
            throw new RepositoryException("Failed to update user.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        Connection c = null;

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("DELETE FROM conferenceuser WHERE UserID = ?;");

            stmt.setLong(1, id);

            stmt.execute();

            LOG.info("Successfully deleted user {}.", id);
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to delete user {}.", id, e);
            throw new RepositoryException("Failed to delete user.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public User getById(Long id) throws RepositoryException {
        Connection c = null;
        User u;

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("SELECT * FROM conferenceuser WHERE UserID = ?;");

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                LOG.warn("Non-existing user with ID {}.", id);
                return null;
            }

            UserBuilder builder = new UserBuilder();
            u = builder.withEmail(rs.getString("Email")).withFirstName(rs.getString("FirstName")).withLastName(rs.getString("LastName")).withPwd(rs.getString("Pwd")).withRole(rs.getString("Role")).inInstitution(rs.getString("Institution")).inPosition(rs.getString("Position")).withDegree(rs.getString("AcademicDegree")).build();
            u.setId(rs.getLong("UserID"));
            u.setUuid(rs.getString("UUID"));

            LOG.info("Successfully retrieved user {}.", u.getId());
            return u;
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to get user by ID.", e);
            throw new RepositoryException("Failed to get user by ID.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public User getByEmail(String email) {
        Connection c = null;
        User u;

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("SELECT * FROM conferenceuser WHERE Email = ?;");

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                LOG.warn("Non-existing user with email {}.", email);
                return null;
            }

            UserBuilder builder = new UserBuilder();
            u = builder.withEmail(rs.getString("Email")).withFirstName(rs.getString("FirstName")).withLastName(rs.getString("LastName")).withPwd(rs.getString("Pwd")).withRole(rs.getString("Role")).inInstitution(rs.getString("Institution")).inPosition(rs.getString("Position")).withDegree(rs.getString("AcademicDegree")).build();
            u.setId(rs.getLong("UserID"));
            u.setUuid(rs.getString("UUID"));

            LOG.info("Successfully retrieved user {}.", u.getId());
            return u;
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to get user by ID.", e);
            throw new RepositoryException("Failed to get user by ID.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public List<User> getAll() {
        Connection c = null;
        ArrayList<User> users = new ArrayList<>();

        try {
            c = connManager.getConnection();
            Statement stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM conferenceuser;");

            User u;
            UserBuilder builder = new UserBuilder();
            while (rs.next()) {
                u = builder.withEmail(rs.getString("Email")).withFirstName(rs.getString("FirstName")).withLastName(rs.getString("LastName")).withPwd(rs.getString("Pwd")).withRole(rs.getString("Role")).inInstitution(rs.getString("Institution")).inPosition(rs.getString("Position")).withDegree(rs.getString("AcademicDegree")).build();
                u.setId(rs.getLong("UserID"));
                u.setUuid(rs.getString("UUID"));

                LOG.info("Successfully retrieved user {}.", u.getId());

                users.add(u);
            }

            return users;
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to get user by ID.", e);
            throw new RepositoryException("Failed to get user by ID.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public List<User> getByRole(Role role) {
        Connection c = null;
        ArrayList<User> users = new ArrayList<>();

        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM conferenceuser WHERE Role = ?;");

            stmt.setString(1, role.toString());

            ResultSet rs = stmt.executeQuery();

            User u;
            UserBuilder builder = new UserBuilder();
            while (rs.next()) {
                u = builder.withEmail(rs.getString("Email")).withFirstName(rs.getString("FirstName")).withLastName(rs.getString("LastName")).withPwd(rs.getString("Pwd")).withRole(rs.getString("Role")).inInstitution(rs.getString("Institution")).inPosition(rs.getString("Position")).withDegree(rs.getString("AcademicDegree")).build();
                u.setId(rs.getLong("UserID"));
                u.setUuid(rs.getString("UUID"));

                LOG.info("Successfully retrieved user {}.", u.getId());

                users.add(u);
            }

            return users;
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to get users by role.", e);
            throw new RepositoryException("Failed to get users by role.");
        } finally {
            if (!Objects.isNull(c)) {
                connManager.returnConnection(c);
            }
        }
    }

}
