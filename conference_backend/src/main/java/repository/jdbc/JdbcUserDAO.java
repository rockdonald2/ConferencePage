package repository.jdbc;

import model.ModelException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.RepositoryException;
import repository.UserDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JdbcUserDAO implements UserDAO {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcUserDAO.class);

    private final ConnectionManager connManager;

    public JdbcUserDAO() {
        connManager = ConnectionManager.getInstance();
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
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to update user.", e);
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
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to delete user.", e);
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

            u = new User();
            u.setId(rs.getLong("UserID"));
            u.setUuid(rs.getString("UUID"));
            u.setEmail(rs.getString("Email"));
            u.setFirstName(rs.getString("FirstName"));
            u.setLastName(rs.getString("LastName"));
            u.setPwd(rs.getString("Pwd"));
            try {
                u.setRole(rs.getString("Role"));
            } catch (ModelException e) {
                LOG.error("Invalid role for user with ID {}; setting to Guest.", id);
                u.setRole("guest");
            }
            u.setInstitution(rs.getString("institution"));
            u.setPosition(rs.getString("position"));
            u.setAcademicDegree(rs.getString("AcademicDegree"));

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

            u = new User();
            u.setId(rs.getLong("UserID"));
            u.setUuid(rs.getString("UUID"));
            u.setEmail(rs.getString("Email"));
            u.setFirstName(rs.getString("FirstName"));
            u.setLastName(rs.getString("LastName"));
            u.setPwd(rs.getString("Pwd"));
            try {
                u.setRole(rs.getString("Role"));
            } catch (ModelException e) {
                LOG.error("Invalid role for user with email {}; setting to Guest.", email);
                u.setRole("guest");
            }
            u.setInstitution(rs.getString("institution"));
            u.setPosition(rs.getString("position"));
            u.setAcademicDegree(rs.getString("AcademicDegree"));

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

            if (!rs.next()) {
                LOG.warn("No users found.");
                return Collections.emptyList();
            }

            User u;
            while (rs.next()) {
                u = new User();
                u.setId(rs.getLong("UserID"));
                u.setUuid(rs.getString("UUID"));
                u.setEmail(rs.getString("Email"));
                u.setFirstName(rs.getString("FirstName"));
                u.setLastName(rs.getString("LastName"));
                u.setPwd(rs.getString("Pwd"));
                try {
                    u.setRole(rs.getString("Role"));
                } catch (ModelException e) {
                    LOG.error("Invalid role for user with email {}; setting to Guest.", u.getEmail());
                    u.setRole("guest");
                }
                u.setInstitution(rs.getString("institution"));
                u.setPosition(rs.getString("position"));
                u.setAcademicDegree(rs.getString("AcademicDegree"));

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
}
