package home.db;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

public class DbCreator {

    private static final Logger LOG = Logger.getLogger(DbCreator.class);

    private static final String DB_FILE_PATH = "database.db";

    /**
     * @return returns the absolute path to the created SQLite database file, if
     *         this file does not exist, then creates it
     */
    public static String dbFileAbsolutePath() throws IOException {
        try {
            File file = new File(DB_FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            LOG.error("Ошибка при создании файла базы данных.", e);
            throw e;
        }
    }

    private DbCreator() {
    }
}
