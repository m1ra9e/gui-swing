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
package home;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import home.db.DbType;
import home.gui.ColorSchema;
import home.utils.CustomProperties;

public final class Settings {

    public enum Setting {

        AUTO_RESIZE_TABLE_WIDTH("gui.auto_resize_table_width", "false"),
        DATABASE("db.database", Const.EMPTY_STRING),
        DATABASE_TYPE("db.type", Const.EMPTY_STRING),
        HOST("db.host", Const.EMPTY_STRING),
        PASSWORD("db.password", Const.EMPTY_STRING),
        PORT("db.port", Const.EMPTY_STRING),
        USER("db.user", Const.EMPTY_STRING),
        STYLE("gui.style", ColorSchema.CROSSPLATFORM.name().toLowerCase(Locale.ROOT));

        private final String name;
        private final String defaultValue;

        private Setting(String name, String defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return name;
        }

        public String getDefaultValue() {
            return defaultValue;
        }
    }

    public enum ResetType {
        SET_DEFAULT, CLEAR;
    }

    private static final String SETTINGS_FILE_NAME = "settings.properties";
    private static final CustomProperties SETTINGS = new CustomProperties();

    public static String getStyle() {
        return get(Setting.STYLE);
    }

    public static boolean isAutoResizeTableWidth() {
        return Boolean.parseBoolean(get(Setting.AUTO_RESIZE_TABLE_WIDTH));
    }

    public static DbType getDatabaseType() throws SQLException {
        String databaseTypeStr = get(Setting.DATABASE_TYPE);
        return DbType.getDbType(databaseTypeStr);
    }

    public static String getHost() {
        return get(Setting.HOST);
    }

    public static int getPort() throws SQLException {
        String port = get(Setting.PORT);
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw new SQLException("Incorrect value of parameter %s: %s"
                    .formatted(Setting.PORT, port));
        }
    }

    public static String getDatabase() {
        return get(Setting.DATABASE);
    }

    public static String getUser() {
        return get(Setting.USER);
    }

    public static String getPassword() {
        return get(Setting.PASSWORD);
    }

    public static boolean hasDatabase() {
        String database = get(Setting.DATABASE);
        return database != null && !database.isBlank();
    }

    private static String get(Setting setting) {
        return SETTINGS.getProperty(setting.getName());
    }

    public static void saveStyle(String style) throws IOException {
        saveSetting(Setting.STYLE, style);
    }

    public static void saveAutoResizeTableWidth(boolean isAutoResizeTableWidth) throws IOException {
        saveSetting(Setting.AUTO_RESIZE_TABLE_WIDTH, String.valueOf(isAutoResizeTableWidth));
    }

    public static void saveDatabaseType(String databaseType) throws IOException {
        saveSetting(Setting.DATABASE_TYPE, databaseType);
    }

    public static void saveDatabase(String database) throws IOException {
        saveSetting(Setting.DATABASE, database);
    }

    private static void saveSetting(Setting setting, String value) throws IOException {
        SETTINGS.setProperty(setting.getName(), value);
        writeSettings();
        readSettings();
    }

    public static void saveDbConnSettings(String host, String port,
            String dbName, String user, String pass, String dbType) throws IOException {
        saveSettings(Map.ofEntries(
                Map.entry(Setting.HOST, host),
                Map.entry(Setting.PORT, port),
                Map.entry(Setting.DATABASE, dbName),
                Map.entry(Setting.USER, user),
                Map.entry(Setting.PASSWORD, pass),
                Map.entry(Setting.DATABASE_TYPE, dbType)));
    }

    public static void saveSettings(Map<Setting, String> settingsMap) throws IOException {
        for (Entry<Setting, String> settingEntry : settingsMap.entrySet()) {
            SETTINGS.setProperty(settingEntry.getKey().getName(), settingEntry.getValue());
        }
        writeSettings();
        readSettings();
    }

    private static void writeSettings() {
        try (var outputStream = new FileOutputStream(SETTINGS_FILE_NAME)) {
            SETTINGS.store(outputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Error while filling the settings file: "
                    + SETTINGS_FILE_NAME, e);
        }
    }

    public static void readSettings() {
        readSettings(SETTINGS_FILE_NAME);
    }

    public static void readSettings(String settingsFileName) {
        try (var inputStream = new FileInputStream(getSettingsPath(settingsFileName))) {
            SETTINGS.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Error while reading settings from file: "
                    + settingsFileName, e);
        }
    }

    private static String getSettingsPath(String settingsFileName) {
        try {
            var file = new File(settingsFileName);
            if (!file.exists()) {
                file.createNewFile();
                fillWithDefaultSettings();
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            throw new IllegalStateException("Error while creating the settings file: "
                    + settingsFileName, e);
        }
    }

    private static void fillWithDefaultSettings() {
        for (Setting setting : Setting.values()) {
            SETTINGS.setProperty(setting.getName(), setting.getDefaultValue());
        }
        writeSettings();
    }

    // clearing DB settings is used only if the user wants to work
    // with a new DB when the application starts
    public static void resetDbSettings(ResetType resetType) throws IOException {
        String styleValue = getStyle();
        String isAutoResizeTableWidthValue = get(Setting.AUTO_RESIZE_TABLE_WIDTH);

        Function<Setting, String> getValueFnc = getSettingValueFunction(resetType);
        for (Setting setting : Setting.values()) {
            SETTINGS.setProperty(setting.getName(), getValueFnc.apply(setting));
        }

        SETTINGS.setProperty(Setting.STYLE.getName(), styleValue);
        SETTINGS.setProperty(Setting.AUTO_RESIZE_TABLE_WIDTH.getName(), isAutoResizeTableWidthValue);

        writeSettings();
        readSettings();
    }

    private static Function<Setting, String> getSettingValueFunction(ResetType resetType) {
        return switch (resetType) {
            case SET_DEFAULT -> setting -> setting.getDefaultValue();
            case CLEAR -> setting -> Const.EMPTY_STRING;
        };
    }

    private Settings() {
    }
}
