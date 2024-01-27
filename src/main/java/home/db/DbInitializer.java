
package home.db;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import home.Settings;

public class DbInitializer {

    private static final Logger LOG = Logger.getLogger(DbInitializer.class);

    private static final String CREATE_TBL_QUERY = "CREATE TABLE if not exists vehicle"
            + " ('id' INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " 'type' TEXT, 'color' TEXT, 'number' TEXT,"
            + " 'is_transports_cargo' INTEGER, 'is_transports_passengers' INTEGER,"
            + " 'has_trailer' INTEGER, 'has_cradle' INTEGER, 'date_time' INTEGER);";

    private DbInitializer() {
    }

    public static void createDbFileIfNotExists(File file) throws IOException {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            Settings.writeSetting(Settings.DB_FILE_PATH_SETTING_NAME, file.getAbsolutePath());
        } catch (IOException e) {
            LOG.error("Error while creating the database file.", e);
            throw e;
        }
    }

    public static void createTableIfNotExists() throws SQLException {
        try (var stmt = Connector.getConnection().createStatement()) {
            stmt.execute(CREATE_TBL_QUERY);
        } finally {
            Connector.closeConnection();
        }
    }
}
