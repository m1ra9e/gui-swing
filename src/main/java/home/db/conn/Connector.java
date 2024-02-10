/*******************************************************************************
 * Copyright 2021-2024 Lenar Shamsutdinov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package home.db.conn;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.Settings;
import home.Settings.ResetType;
import home.db.DbType;
import home.gui.GuiConst;
import home.utils.LogUtils;

public final class Connector {

    private static final Logger LOG = LoggerFactory.getLogger(Connector.class);

    private static ConnectionData connectionData;

    // resetting the connection settings cache is used only when changing the DB
    public static void resetConnectionDataAndSettings() throws IOException {
        Settings.resetDbSettings(ResetType.CLEAR);
        connectionData = null;
    }

    public static boolean testConnection(String host, int port, String dbName,
            String user, String pass, DbType dbType) throws SQLException {
        if (dbType == DbType.PostgreSQL) {
            ConnectionData connectionData = new PgConnectionDataHelper(host, port, dbName, user, pass)
                    .getConnectionData();
            return testConnection(connectionData);
        }

        throw new SQLException(GuiConst.CONNECTION_TEST_SUPPORT_ERROR_TEXT.formatted(dbType));
    }

    public static boolean testCurrentConnection() throws SQLException {
        ConnectionData connData = getConnectionData();
        return testConnection(connData);
    }

    private static boolean testConnection(ConnectionData connData) throws SQLException {
        try (Connection conn = getConnection(connData)) {
            return true;
        } catch (SQLException e) {
            throw new SQLException(GuiConst.CONNECTION_TEST_ERROR_TEXT.formatted(e.getMessage()), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        ConnectionData connData = getConnectionData();
        return getConnection(connData);
    }

    private static ConnectionData getConnectionData() throws SQLException {
        if (connectionData != null) {
            return connectionData;
        }

        DbType dbType = Settings.getDatabaseType();

        AbstractConnectionDataHelper connectionDataHelper = switch (dbType) {
            case PostgreSQL -> new PgConnectionDataHelper(Settings.getHost(),
                    Settings.getPort(), Settings.getDatabase(),
                    Settings.getUser(), Settings.getPassword());
            case SQLite -> new SQLiteConnectionDataHelper(Settings.getDatabase());
        };

        connectionData = connectionDataHelper.getConnectionData();

        return connectionData;
    }

    private static Connection getConnection(ConnectionData connData) throws SQLException {
        return getConnection(connData.url(), connData.connProps(), connData.jdbcDriver());
    }

    private static Connection getConnection(String url, Properties props, String jdbcDriver)
            throws SQLException {
        try {
            Class.forName(jdbcDriver);

            // Driver driver = (Driver) Class.forName(jdbcDriver).newInstance();
            // DriverManager.registerDriver(driver);

            return DriverManager.getConnection(url, props);
        } catch (ClassNotFoundException e) {
            throw LogUtils.logAndCreateSqlException("Database driver class not found.", LOG, e);
        } catch (SQLException e) {
            throw LogUtils.logAndCreateSqlException("Error while connecting to the database. %s"
                    .formatted(e.getMessage()), LOG, e);
        }
    }

    private Connector() {
    }
}
