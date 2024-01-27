
package home.db;

import java.sql.SQLException;

public class DbInitializer {

    private static final String CREATE_TBL_QUERY = "CREATE TABLE if not exists vehicle"
            + " ('id' INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " 'type' TEXT, 'color' TEXT, 'number' TEXT,"
            + " 'is_transports_cargo' INTEGER, 'is_transports_passengers' INTEGER,"
            + " 'has_trailer' INTEGER, 'has_cradle' INTEGER, 'date_time' INTEGER);";

    public static void createTableIfNotExists() throws SQLException {
        try (var stmt = Connector.getConnection().createStatement()) {
            stmt.execute(CREATE_TBL_QUERY);
        } finally {
            Connector.closeConnection();
        }
    }

    private DbInitializer() {
    }
}
