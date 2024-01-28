package home.db;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.Settings;
import home.Settings.Setting;

public final class DbInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(DbInitializer.class);

    private static final String CREATE_TBL_QUERY = "CREATE TABLE if not exists vehicle"
            + " ('id' INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " 'type' TEXT, 'color' TEXT, 'number' TEXT,"
            + " 'is_transports_cargo' INTEGER, 'is_transports_passengers' INTEGER,"
            + " 'has_trailer' INTEGER, 'has_cradle' INTEGER, 'date_time' INTEGER);";

    public static void createDbFileIfNotExists(File file) throws IOException {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            Settings.writeSetting(Setting.DB_FILE_PATH, file.getAbsolutePath());
        } catch (IOException e) {
            LOG.error("Error while creating the database file.", e);
            throw new IOException("Error while creating the database file.", e);
        }
    }

    public static void createTableIfNotExists() throws SQLException {
        try (var conn = Connector.getConnectionToSQLite();
                var stmt = conn.createStatement()) {
            stmt.execute(CREATE_TBL_QUERY);
        }
    }

    private DbInitializer() {
    }
}
