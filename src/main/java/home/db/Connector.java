
package home.db;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.Settings;
import home.utils.LogUtils;

public final class Connector {

    private static final Logger LOG = LoggerFactory.getLogger(Connector.class);

    private static final String QUERY_TIMEOUT = "30";

    private static final String JDBC_DRIVER_POSTGRESQL = "org.postgresql.Driver";
    private static final String URL_POSTGRESQL = "jdbc:postgresql://%s:%s/%s";

    private static final String JDBC_DRIVER_SQLITE = "org.sqlite.JDBC";
    private static final String URL_SQLITE = "jdbc:sqlite:%s";

    // TODO add PostgreSQL
    public static Connection getConnectionToPostgreSQL(String host, String port,
            String dbName, String user, String password) throws SQLException {
        String url = generatePostgreSqlURL(host, port, dbName);

        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        props.setProperty("reWriteBatchedInserts", "true");
        props.setProperty("loginTimeout", QUERY_TIMEOUT);
        props.setProperty("connectTimeout", QUERY_TIMEOUT);
        props.setProperty("cancelSignalTimeout", QUERY_TIMEOUT);
        props.setProperty("socketTimeout", QUERY_TIMEOUT);

        return getConnection(url, props, JDBC_DRIVER_POSTGRESQL);
    }

    private static String generatePostgreSqlURL(String host, String port, String dbName) {
        String db;
        try {
            db = URLEncoder.encode(dbName, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOG.error("Encoding error of database name.", ex);
            db = dbName;
        }
        return String.format(URL_POSTGRESQL, host, port, db);
    }

    public static Connection getConnectionToSQLite() throws SQLException {
        return getConnection(String.format(URL_SQLITE, Settings.getDbFilePath()),
                new Properties(), JDBC_DRIVER_SQLITE);
    }

    private static Connection getConnection(String url, Properties props, String jdbcDriver)
            throws SQLException {
        try {
            Class.forName(jdbcDriver);

            // Driver driver = (Driver)
            // Class.forName(jdbcDriver).newInstance();
            // DriverManager.registerDriver(driver);

            return DriverManager.getConnection(url, props);
        } catch (ClassNotFoundException e) {
            throw LogUtils.logAndCreateSqlException("Database driver class not found.", LOG, e);
        } catch (SQLException e) {
            throw LogUtils.logAndCreateSqlException("Error while connecting to the database.", LOG, e);
        }
    }

    private Connector() {
    }
}
