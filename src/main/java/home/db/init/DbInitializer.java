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
package home.db.init;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import home.Settings;
import home.db.DbType;

public final class DbInitializer {

    private static AbstractDbInitializer initializer;
    private static DbType previousDbType;

    public static void createDbFileIfNotExists(File file) throws IOException, SQLException {
        Settings.saveDatabaseType(DbType.SQLite.name());
        ((SQLiteDbInitializer) getInitializer()).createDbFileIfNotExists(file);
    }

    public static void createTableIfNotExists() throws SQLException {
        getInitializer().createTableIfNotExists();
    }

    private static AbstractDbInitializer getInitializer() throws SQLException {
        DbType currentDbType = Settings.getDatabaseType();

        if (initializer != null && previousDbType == currentDbType) {
            return initializer;
        }

        initializer = switch (currentDbType) {
            case SQLite -> new SQLiteDbInitializer();
            case PostgreSQL -> new PgDbInitializer();
        };
        previousDbType = currentDbType;

        return initializer;
    }

    private DbInitializer() {
    }
}
