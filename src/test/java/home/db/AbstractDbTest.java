/*******************************************************************************
 * Copyright 2021-2026 Lenar Shamsutdinov
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

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import home.Settings;
import home.Settings.Setting;
import home.db.conn.Connector;
import home.db.init.DbInitializer;

// "@TestInstance(TestInstance.Lifecycle.PER_CLASS)" added to use "@BeforeAll"
// and "@AfterAll" without static modifier.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract sealed class AbstractDbTest permits AbstractDmlTest, AbstractDdlTest {

    private static final IDbTestPreparer SQLITE_PREPARER = new SqliteTestPreparer();
    private static final IDbTestPreparer PG_PREPARER = new PgTestPreparer();

    @BeforeAll
    void init() throws Exception {
        basePrepare();
        if (isAvailableWorkWithDb()) {
            getPreparer().init();
        }
    }

    // Needed for tests to work correctly with @SkipIfDbUnavailable.
    private void basePrepare() throws Exception {
        Settings.saveSettings(Map.ofEntries(
                Map.entry(Setting.DATABASE_TYPE, getDbType().name()),
                Map.entry(Setting.STYLE, Setting.STYLE.getDefaultValue()),
                Map.entry(Setting.AUTO_RESIZE_TABLE_WIDTH,
                        Setting.AUTO_RESIZE_TABLE_WIDTH.getDefaultValue())));
        resetConnection();
    }

    protected abstract DbType getDbType();

    protected void resetConnection() throws Exception {
        Field connectionDataField = Connector.class.getDeclaredField("connectionData");
        connectionDataField.setAccessible(true);
        connectionDataField.set(null, null);
    }

    @AfterAll
    void cleanUp() throws Exception {
        if (isAvailableWorkWithDb()) {
            getPreparer().cleanUp();
        }
    }

    private boolean isAvailableWorkWithDb() throws Exception {
        return getPreparer().isAvailableWorkWithDb();
    }

    protected String getTableName() throws Exception {
        return getPreparer().getTableName();
    }

    private IDbTestPreparer getPreparer() throws SQLException {
        DbType dbType = getDbType();
        return switch (dbType) {
            case SQLite -> SQLITE_PREPARER;
            case PostgreSQL -> PG_PREPARER;
            default -> throw new SQLException("Unsupported database type : " + dbType);
        };
    }

    protected String getDbFileName() {
        return getValueWithTypeCheck(((SqliteTestPreparer) SQLITE_PREPARER)::getDbFileName);
    }

    protected File getGeneratedDbFile() {
        return getValueWithTypeCheck(((SqliteTestPreparer) SQLITE_PREPARER)::getGeneratedDbFile);
    }

    private <T> T getValueWithTypeCheck(Supplier<T> getter) {
        DbType dbType = getDbType();
        if (dbType == DbType.SQLite) {
            return getter.get();
        }

        throw new IllegalArgumentException(
                "%s is not file database (db file not used)".formatted(dbType));
    }
}

sealed interface IDbTestPreparer permits PgTestPreparer, SqliteTestPreparer {

    void init() throws Exception;

    void cleanUp() throws Exception;

    boolean isAvailableWorkWithDb();

    String getTableName() throws Exception;
}

final class PgTestPreparer implements IDbTestPreparer {

    private static final String PG_TEST_SETTINGS = "pg_test_settings.properties";

    private static final String PG_DATA_TABLE = "public.vehicle";

    @Override
    public void init() throws Exception {
        Settings.readSettings(getPgTestSettingPath());
        try {
            if (Connector.testCurrentConnection()) {
                DbTestUtils.drop(PG_DATA_TABLE);
                DbInitializer.createTableIfNotExists();
            }
        } catch (SQLException e) {
            fail("Errors while create table in DB.", e);
        }
    }

    private String getPgTestSettingPath() {
        URL testSettingsUrl = AbstractDbTest.class.getResource(PG_TEST_SETTINGS);
        return testSettingsUrl.getPath();
    }

    @Override
    public void cleanUp() throws Exception {
        DbTestUtils.drop(PG_DATA_TABLE);
        Connector.resetConnectionDataAndSettings();
    }

    @Override
    public boolean isAvailableWorkWithDb() {
        try {
            Settings.readSettings(getPgTestSettingPath());
            return Connector.testCurrentConnection();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getTableName() throws Exception {
        return PG_DATA_TABLE;
    }
}

final class SqliteTestPreparer extends AbstractFileDatabaseTestPreparer implements IDbTestPreparer {

    private static final String DB_FILE_NAME = "database.db";
    private static final String SQLITE_DATA_TABLE = "vehicle";

    private static File generatedDbFile;

    @Override
    public void init() throws Exception {
        AbstractFileDatabaseTestPreparer.deletePreviousTmpFiles(getTmpPrefix(), DB_FILE_NAME);
        try {
            Settings.readSettings();
            generatedDbFile = File.createTempFile(getTmpPrefix(), DB_FILE_NAME);
            DbInitializer.createDbFileIfNotExists(generatedDbFile);
            DbInitializer.createTableIfNotExists();
        } catch (IOException e) {
            fail("Errors while create DB file.", e);
        } catch (SQLException e) {
            fail("Errors while create table in DB.", e);
        }
    }

    @Override
    public void cleanUp() throws Exception {
        try {
            Files.deleteIfExists(generatedDbFile.toPath());
            Connector.resetConnectionDataAndSettings();
        } catch (IOException e) {
            fail("Errors while delete DB file.", e);
        }
    }

    @Override
    public boolean isAvailableWorkWithDb() {
        return AbstractFileDatabaseTestPreparer.isAvailableWorkWithTempFile();
    }

    @Override
    public String getTableName() throws Exception {
        return SQLITE_DATA_TABLE;
    }

    String getDbFileName() {
        return DB_FILE_NAME;
    }

    File getGeneratedDbFile() {
        return generatedDbFile;
    }
}

abstract sealed class AbstractFileDatabaseTestPreparer permits SqliteTestPreparer {

    private static final String TEMP_DIR_PROPERTY = "java.io.tmpdir";
    private static final String TMP_PREFIX = "tmp_";
    private static final String AVAILABILITY_CHECK_FILE_NAME = "availability_check.db";

    String getTmpPrefix() {
        return TMP_PREFIX;
    }

    static boolean isAvailableWorkWithTempFile() {
        try {
            deletePreviousTmpFiles(TMP_PREFIX, AVAILABILITY_CHECK_FILE_NAME);
            File generatedAvailabilityCheckFile = File.createTempFile(TMP_PREFIX, AVAILABILITY_CHECK_FILE_NAME);
            return Files.deleteIfExists(generatedAvailabilityCheckFile.toPath());
        } catch (IOException e) {
            return false;
        }
    }

    static void deletePreviousTmpFiles(String prefix, String suffix) {
        File tmpDir = new File(System.getProperty(TEMP_DIR_PROPERTY));
        for (File file : tmpDir.listFiles()) {
            String tmpFileName = file.getName();
            if (file.isFile() && tmpFileName.startsWith(prefix)
                    && tmpFileName.contains(suffix)) {
                file.delete();
            }
        }
    }
}
