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
package home.db;

import java.sql.SQLException;

public enum DbType {

    // TODO add support for MS SQL Server
    // MS_SQL_Server("com.microsoft.sqlserver.jdbc.SQLServerDriver",
    // "jdbc:sqlserver://%s:%d;DatabaseName=%s"),
    PostgreSQL("org.postgresql.Driver", "jdbc:postgresql://%s:%d/%s"),
    SQLite("org.sqlite.JDBC", "jdbc:sqlite:%s");

    private final String jdbcDriver;
    private final String url;

    private DbType(String jdbcDriver, String url) {
        this.jdbcDriver = jdbcDriver;
        this.url = url;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    /**
     * Gets the URL of the database
     *
     * <pre>
     * {@code
     * DbType.PostgreSQL.getUrl("host", "port", "db");
     * DbType.SQLite.getUrl("db");
     * }
     * </pre>
     *
     * @param args database url parameters
     * @return database url
     */
    public String getUrl(Object... args) {
        return url.formatted(args);
    }

    public boolean in(DbType... dbTypes) {
        for (var dbType : dbTypes) {
            if (this == dbType) {
                return true;
            }
        }
        return false;
    }

    public static DbType getDbType(String type) throws SQLException {
        String typeFormatted = type.strip();
        for (DbType dbType : DbType.values()) {
            if (typeFormatted.equalsIgnoreCase(dbType.name())) {
                return dbType;
            }
        }

        throw new SQLException("Unsupported database type : " + type);
    }
}
