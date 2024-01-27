
package home.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class Connector {

    private static final Logger LOG = Logger.getLogger(Connector.class);

    private static final String JDBC_DRIVER_SQLITE = "org.sqlite.JDBC";
    private static final String CONNECTION_URL_SQLITE = "jdbc:sqlite:%s";

    private static String dbFileAbsolutePath;
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                if (dbFileAbsolutePath == null) {
                    dbFileAbsolutePath = DbCreator.dbFileAbsolutePath();
                }

                Class.forName(JDBC_DRIVER_SQLITE);

                // Driver driver = (Driver) Class.forName(JDBC_DRIVER_SQLITE).newInstance();
                // DriverManager.registerDriver(driver);

                connection = DriverManager.getConnection(
                        String.format(CONNECTION_URL_SQLITE, dbFileAbsolutePath));
            } catch (ClassNotFoundException | IOException e) {
                String errorMsg = null;
                if (e instanceof ClassNotFoundException) {
                    errorMsg = "Database driver class not found.";
                } else if (e instanceof IOException) {
                    errorMsg = "Error while creating the database file.";
                }
                LOG.error(errorMsg);
                SQLException ex = new SQLException(errorMsg);
                ex.addSuppressed(e);
                throw ex;
            } catch (SQLException e) {
                LOG.error("Error while connecting to the database.");
                throw e;
            }
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            LOG.error("Error while closing DB connection.");
            throw e;
        }
    }

    private Connector() {
    }
}
