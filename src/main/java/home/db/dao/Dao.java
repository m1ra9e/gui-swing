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
package home.db.dao;

import java.sql.SQLException;
import java.util.List;

import home.Settings;
import home.db.DbType;
import home.model.AbstractVehicle;

public final class Dao {

    private static IDao dao;
    private static DbType previousDbType;

    public static List<AbstractVehicle> readAll() throws SQLException {
        return getDao().readAll();
    }

    @Deprecated(forRemoval = true) // because it uses only in test
    public static AbstractVehicle readOne(long id) throws SQLException {
        return getDao().readOne(id);
    }

    public static void saveAllChanges() throws SQLException {
        getDao().saveAllChanges();
    }

    public static void saveAs() throws SQLException {
        getDao().saveAs();
    }

    private static IDao getDao() throws SQLException {
        DbType currentDbType = Settings.getDatabaseType();

        if (dao != null && previousDbType == currentDbType) {
            return dao;
        }

        dao = switch (currentDbType) {
            case SQLite -> new SQLiteDao();
            case PostgreSQL -> new PgDao();
        };
        previousDbType = currentDbType;

        return dao;
    }

    private Dao() {
    }
}
