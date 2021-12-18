package repository.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.RepositoryException;
import util.PropertyProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ConnectionManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);

    private static ConnectionManager instance;
    private List<Connection> pool;
    private static final int POOL_SIZE = Integer.parseInt(PropertyProvider.getProperty("db.pool.size"));

    private ConnectionManager() throws RepositoryException {
        try {
            Class.forName(PropertyProvider.getProperty("db.driver"));

            pool = new LinkedList<>();

            for (int i = 0; i < POOL_SIZE; ++i) {
                pool.add(DriverManager.getConnection(PropertyProvider.getProperty("db.url"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.pwd")));
            }

            LOG.info("Successfully initialized connection pool with {} connections.", POOL_SIZE);
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error("Failed to initialize connection pool.", e);
            throw new RepositoryException("Failed to initialize connection pool.");
        }
    }

    public synchronized static ConnectionManager getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ConnectionManager();
        }

        return instance;
    }

    public synchronized Connection getConnection() throws RepositoryException {
        if (pool.size() == 0) throw new RepositoryException("Empty connection pool.");

        Connection c = pool.get(0);
        pool.remove(0);

        return c;
    }

    public synchronized void returnConnection(Connection connection) {
        if (pool.size() < POOL_SIZE) {
            pool.add(connection);
        }
    }

}
