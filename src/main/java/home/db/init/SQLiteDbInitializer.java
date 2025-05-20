/*******************************************************************************
 * Copyright 2021-2025 Lenar Shamsutdinov
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.Settings;

final class SQLiteDbInitializer extends AbstractDbInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(SQLiteDbInitializer.class);

    private static final String CREATE_TABLE_QUERY = """
            CREATE TABLE IF NOT EXISTS vehicle (
                'id' INTEGER PRIMARY KEY AUTOINCREMENT,
                'type' TEXT,
                'color' TEXT,
                'number' TEXT,
                'is_transports_cargo' INTEGER,
                'is_transports_passengers' INTEGER,
                'has_trailer' INTEGER,
                'has_cradle' INTEGER,
                'date_time' INTEGER)
            """;

    @Override
    protected String getTableCreationQuery() {
        return CREATE_TABLE_QUERY;
    }

    void createDbFileIfNotExists(File file) throws IOException {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            Settings.saveDatabase(file.getAbsolutePath());
        } catch (IOException e) {
            LOG.error("Error while creating the database file.", e);
            throw new IOException("Error while creating the database file.", e);
        }
    }
}
