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
package home.gui;

public final class GuiConst {

    public static final String DATE_FORMAT = "yyyy.MM.dd | HH:mm:ss";

    // label format "postgresql://127.0.0.1:5432/database_name"
    public static final String DB_LABEL_FORMAT = "%s://%s:%d/%s";

    // Table column names
    public static final String TYPE = "Type";
    public static final String COLOR = "Color";
    public static final String NUMBER = "Number";
    public static final String DATE = "Date";
    public static final String DELETION_MARK = "Deletion mark";

    // Button names
    static final String CAR = "Car";
    static final String TRUCK = "Truck";
    static final String MOTORCYCLE = "Motorcycle";
    static final String DELETE = "Delete";
    public static final String OK = "Ok";
    public static final String TEST_CONNECTION = "Test connection";
    public static final String SAVE_AND_CONNECT = "Save and connect";
    public static final String CANCEL = "Cancel";
    public static final String HAS_TRAILER = "has trailer";
    public static final String TRANSPORTS_PASSENGERS = "transports passengers";
    public static final String TRANSPORTS_CARGO = "transports cargo";
    public static final String HAS_CRADLE = "has cradle";

    // Menu names
    static final String FILE = "File";
    static final String IMPORT_FROM = "Import from ...";
    static final String EXPORT_TO = "Export to ...";
    static final String STYLE = "Style";
    static final String HELP = "Help";
    static final String ABOUT = "About";
    public static final String CREATE_OR_OPEN_FILE_DATABASE = "Create/Open file database";
    public static final String CONNECT_TO_SERVER_DATABASE = "Connect to server database";
    public static final String SAVE = "Save";
    public static final String SAVE_AS = "Save as ... (to file)";
    public static final String AUTO_RESIZE_TABLE_WIDTH = "Auto resize table width";

    // DB label
    public static final String DATABASE_NOT_SELECTED = "Database not selected";

    // DB Connection dialog labels
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String DB_NAME = "database name";
    public static final String USER = "user";
    public static final String PASS = "password";
    public static final String SHOW_PASS = "show password";
    public static final String DB_TYPE = "database type";

    // About dialog text
    static final String ABOUT_TITLE = "About";
    static final String ABOUT_TEXT = "Test application.\nVersion: %s";

    // Save dialog text
    public static final String SAVE_TITLE = "Saving";
    public static final String SAVE_TEXT = "Saved successfully";

    // Save to already exists dialog text
    public static final String ALREADY_EXISTS_TITLE = "Such file already exists";
    public static final String ALREADY_EXISTS_TEXT = "File with such name already exists, text another name.";

    // Continue previous database dialog text
    public static final String PREVIOUS_DATABASE_TITLE = "Previous database";
    public static final String PREVIOUS_DATABASE_TEXT = "Continue with previous database?\n\nPrevious database:\n%s";

    // Removed previous connection dialog text
    public static final String REMOVED_PREVIOUS_CONNECTION_TITLE = "Removed previous connection";
    public static final String REMOVED_PREVIOUS_CONNECTION_TEXT = "Connection to previous database was removed.";

    // Operation confirm dialog text
    public static final String OPERATION_CONFIRM_TITLE = "Operation confirm";
    public static final String OPERATION_CONFIRM_TEXT = "Do you want to %s ?";

    // Connection test successful dialog text
    public static final String CONNECTION_TEST_SUCCESSFUL_TITLE = "Connection test";
    public static final String CONNECTION_TEST_SUCCESSFUL_TEXT = "Connection successful!";

    // Connection test error message
    public static final String CONNECTION_TEST_ERROR_TEXT = "Connection test error:\n%s";

    // Connection test supported error message
    public static final String CONNECTION_TEST_SUPPORT_ERROR_TEXT = "Connection test for db %s is not supported";

    private GuiConst() {
    }
}
