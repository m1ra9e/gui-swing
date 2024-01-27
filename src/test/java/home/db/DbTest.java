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

import home.Settings;
import home.db.dao.DaoSQLite;
import home.models.AbstractVehicle;
import home.models.Car;

public class DbTest {

    private static final String DB_FILE_NAME = "database.db";

    private static final String TMP = "tmp_";

    private static File generatedDbFile;

    @BeforeEach
    public void initializeTemporaryDbFile() {
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
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        for (File file : tmpDir.listFiles()) {
            String tmpFileName = file.getName();
            if (file.isFile() && tmpFileName.startsWith(TMP)
                    && tmpFileName.contains(DB_FILE_NAME)) {
                file.delete();
            }
        }
    }

    @Test
    public void createDbFileTest() {
        try (var sampleDbFileStream = getClass().getResourceAsStream(DB_FILE_NAME)) {
            byte[] sampleDbFileByte = sampleDbFileStream.readAllBytes();
            byte[] generatedDbFileByte = Files.readAllBytes(generatedDbFile.toPath());
            assertNotNull(sampleDbFileByte, "Sample Db file is null.");
            assertNotNull(generatedDbFileByte, "Generated Db file is null.");
            assertArrayEquals(sampleDbFileByte, generatedDbFileByte);
        } catch (IOException e) {
            fail("Errors while read file.", e);
        }
    }

    @Test
    public void createReadDataTest() {
        try {
            var dataObj = new Car();
            dataObj.setId(1);
            dataObj.setColor("green");
            dataObj.setNumber("n555rt");
            dataObj.setDateTime(System.currentTimeMillis());
            dataObj.setTransportsPassengers(true);
            DaoSQLite.getInstance().create(dataObj);

            AbstractVehicle readDataObj = DaoSQLite.getInstance().readOne(1);

            assertNotNull(readDataObj, "Read data object is null.");
            assertEquals(dataObj, readDataObj);
        } catch (SQLException e) {
            fail("Errors while work with DB.", e);
        }
    }

    @AfterEach
    public void removeTemporaryDbFile() {
        try {
            Files.deleteIfExists(generatedDbFile.toPath());
            Settings.writeSetting(Settings.DB_FILE_PATH_SETTING_NAME, "");
        } catch (IOException e) {
            fail("Errors while delete DB file.", e);
        }
    }
}
