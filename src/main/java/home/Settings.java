package home;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import home.utils.Utils;

public class Settings {

    public static String STYLE;
    public static String DB_FILE_PATH;

    public static final String STYLE_SETTING_NAME = "style";
    public static final String DB_FILE_PATH_SETTING_NAME = "db_file_path";

    private static final String SETTINGS_FILE_NAME = "settings.properties";
    private static final Properties SETTINGS = new Properties();

    private Settings() {
    }

    public static boolean hasPathToDbFile() {
        return Settings.DB_FILE_PATH != null && !Settings.DB_FILE_PATH.isBlank();
    }

    public static void writeSetting(String name, String value) throws IOException {
        SETTINGS.setProperty(name, value);
        try (OutputStream outputStream = new FileOutputStream(SETTINGS_FILE_NAME)) {
            SETTINGS.store(outputStream, null);
        } catch (IOException e) {
            throw Utils.getNewException(e, "Error while filling the settings file.");
        }
        readSettings();
    }

    public static void readSettings() throws IOException {
        try (var inputStream = new FileInputStream(getSettingsPath())) {
            SETTINGS.load(inputStream);
        } catch (IOException e) {
            throw Utils.getNewException(e, "Error while reading settings from file: "
                    + SETTINGS_FILE_NAME);
        }

        STYLE = SETTINGS.getProperty(STYLE_SETTING_NAME, Default.STYLE);
        DB_FILE_PATH = SETTINGS.getProperty(DB_FILE_PATH_SETTING_NAME, Default.DB_FILE_PATH);
    }

    public static String getSettingsPath() throws IOException {
        try {
            File file = new File(SETTINGS_FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
                fillWithDefaultSettings();
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            throw Utils.getNewException(e, "Error while creating the settings file.");
        }
    }

    private static void fillWithDefaultSettings() throws IOException {
        try (OutputStream outputStream = new FileOutputStream(SETTINGS_FILE_NAME)) {
            SETTINGS.setProperty(STYLE_SETTING_NAME, Default.STYLE);
            SETTINGS.setProperty(DB_FILE_PATH_SETTING_NAME, Default.DB_FILE_PATH);
            SETTINGS.store(outputStream, null);
        } catch (IOException e) {
            throw Utils.getNewException(e, "Error while filling the settings file.");
        }
    }

    private final class Default {
        private static final String STYLE = "default";
        private static final String DB_FILE_PATH = "";
    }
}
