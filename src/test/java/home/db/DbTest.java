package home.db;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import home.IConsts;
import home.Settings;
import home.Settings.Setting;
import home.Storage;
import home.db.dao.DaoSQLite;
import home.models.AbstractVehicle;
import home.models.Car;

final class DbTest {

    private static final String TEMP_DIR_PROPERTY = "java.io.tmpdir";

    private static final String TMP = "tmp_";
    private static final String DB_FILE_NAME = "database.db";

    private static File generatedDbFile;

    @BeforeEach
    void initializeTemporaryDbFile() {
        deletePreviousTmpFiles();
        try {
            generatedDbFile = File.createTempFile(TMP, DB_FILE_NAME);
            DbInitializer.createDbFileIfNotExists(generatedDbFile);
            DbInitializer.createTableIfNotExists();
        } catch (IOException e) {
            fail("Errors while create DB file.", e);
        } catch (SQLException e) {
            fail("Errors while create table in DB.", e);
        }
    }

    private static void deletePreviousTmpFiles() {
        File tmpDir = new File(System.getProperty(TEMP_DIR_PROPERTY));
        for (File file : tmpDir.listFiles()) {
            String tmpFileName = file.getName();
            if (file.isFile() && tmpFileName.startsWith(TMP)
                    && tmpFileName.contains(DB_FILE_NAME)) {
                file.delete();
            }
        }
    }

    @Test
    void createDbFileTest() {
        try (var sampleDbFileStream = getClass().getResourceAsStream(DB_FILE_NAME)) {
            byte[] sampleDbFileByte = sampleDbFileStream.readAllBytes();
            assertNotNull(sampleDbFileByte, "Sample Db file is null.");

            byte[] generatedDbFileByte = Files.readAllBytes(generatedDbFile.toPath());
            assertNotNull(generatedDbFileByte, "Generated Db file is null.");

            assertArrayEquals(sampleDbFileByte, generatedDbFileByte);
        } catch (IOException e) {
            fail("Errors while read file.", e);
        }
    }

    @Test
    void createReadDataTest() {
        try {
            // There is no id yet in new data object. Id will be set in database.
            // That is why we will set it later, from read database object.
            // It's need for correct comparison.
            var createdDataObj = new Car();
            createdDataObj.setColor("green");
            createdDataObj.setNumber("n555rt");
            createdDataObj.setDateTime(System.currentTimeMillis());
            createdDataObj.setTransportsPassengers(true);

            Storage.INSTANCE.updateStorage(createdDataObj, Storage.NO_ROW_IS_SELECTED);
            DaoSQLite.getInstance().saveAllChanges();

            long id = 1;
            AbstractVehicle readedDataObj = DaoSQLite.getInstance().readOne(id);

            assertNotNull(readedDataObj, "Read data object is null.");

            // As said before, here we set id from read database object.
            createdDataObj.setId(readedDataObj.getId());

            assertEquals(createdDataObj, readedDataObj);
        } catch (SQLException e) {
            fail("Errors while work with DB.", e);
        }
    }

    @AfterEach
    void removeTemporaryDbFile() {
        try {
            Files.deleteIfExists(generatedDbFile.toPath());
            Settings.writeSetting(Setting.DB_FILE_PATH, IConsts.EMPTY_STRING);
        } catch (IOException e) {
            fail("Errors while delete DB file.", e);
        }
    }
}
