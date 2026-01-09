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
package home.gui.component;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import home.Settings;
import home.db.conn.Connector;
import home.db.init.DbInitializer;
import home.gui.DbOperation;
import home.gui.GuiConst;
import home.gui.exception.CreateOpenSaveCancelException;
import home.gui.exception.SaveAsCancelException;
import home.gui.exception.SaveToAlreadyExistsFileException;

@SuppressWarnings("serial")
public final class CustomJFileChooserDb extends JFileChooser {

    private static final File APPLICATION_DIR = new File(".");
    private static final String EXTENSION_DESCRIPTIONS = "SQLite DB (*.db, *.sqlite, *.sqlite3)";
    private static final String[] EXTENSIONS = { "db", "sqlite", "sqlite3" };

    private static final String DEFAULT_EXTENSION = ".db";
    private static final String DEFAULT_PREFIX = "default_";

    private static final String CHOOSE_STORAGE = "Choose storage";
    private static final String TYPE_NAME_OR_CHOOSE_DB_FILE = "Type a new file"
            + " name or choose already existed SQLite DB file.";

    private static final String DEFAULT_STORAGE = "Default storage";
    private static final String WILL_CREATE_DEFAULT_STORAGE = "The default storage will be created:\n %s";

    private static final int MAX_TRY_COUNT_BEFORE_CREATE_DEFAULT_FILE = 1;
    private int counterBeforeCreateDefaultFile;

    private CustomJFileChooserDb() {
        super(APPLICATION_DIR);
    }

    public static void createAndShowChooser(Component parent, DbOperation operation)
            throws IOException, SQLException {
        var fileChooser = new CustomJFileChooserDb();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                EXTENSION_DESCRIPTIONS, EXTENSIONS));
        fileChooser.showChooser(parent, operation);
    }

    private void showChooser(Component parent, DbOperation operation)
            throws IOException, SQLException {
        int chooserState = showDialog(parent, operation.getOperatioText());
        if (JFileChooser.APPROVE_OPTION == chooserState) {
            //// [Create / Open] button pressed
            counterBeforeCreateDefaultFile = 0;
            File file = getSelectedFile();
            file = addExtensionToFileIfNotExists(file);
            if (DbOperation.CREATE_OR_OPEN_FILE_DATABASE != operation) {
                checkSaveToAlreadyExistsFile(file);
            }
            Connector.resetConnectionDataAndSettings();
            DbInitializer.createDbFileIfNotExists(file);
        } else if (JFileChooser.APPROVE_OPTION != chooserState && DbOperation.SAVE_AS == operation) {
            //// [Cancel] button pressed while [Save as...]
            throw new SaveAsCancelException("Cancel SaveAs action.");
        } else if (JFileChooser.APPROVE_OPTION != chooserState && !Settings.hasDatabase()) {
            //// [Cancel] button pressed during the action [Create / Open] or [Save],
            //// while no database was selected before
            ////
            //// Condition of this block is necessary so that when entering the
            //// [Create / Open] or [Save] menu, you don't need to select database file,
            //// if it already opened or if you don't want to do it.

            if (!isNeedToDoOperation(parent, operation)) {
                //// If user don't want to do Create/Open/Save operation.
                throw new CreateOpenSaveCancelException("Cancel %s action.".formatted(operation));
            }

            if (MAX_TRY_COUNT_BEFORE_CREATE_DEFAULT_FILE == counterBeforeCreateDefaultFile) {
                generateDefaultDbFile(parent);
                return;
            }
            counterBeforeCreateDefaultFile++;
            JOptionPane.showMessageDialog(parent, TYPE_NAME_OR_CHOOSE_DB_FILE,
                    CHOOSE_STORAGE, JOptionPane.WARNING_MESSAGE);
            showChooser(parent, operation);
        }
    }

    private File addExtensionToFileIfNotExists(File file) {
        if (Arrays.stream(EXTENSIONS)
                .anyMatch(extension -> file.getName().endsWith('.' + extension))) {
            return file;
        }

        return new File(file.getAbsolutePath() + DEFAULT_EXTENSION);
    }

    private void checkSaveToAlreadyExistsFile(File file) {
        if (file.exists()) {
            throw new SaveToAlreadyExistsFileException("File '"
                    + file.getAbsolutePath() + "' already exists.");
        }
    }

    private static boolean isNeedToDoOperation(Component parent, DbOperation operation) {
        int dialogResult = JOptionPane.showConfirmDialog(parent,
                GuiConst.OPERATION_CONFIRM_TEXT.formatted(operation.getOperatioText()),
                GuiConst.OPERATION_CONFIRM_TITLE, JOptionPane.YES_NO_OPTION);
        return dialogResult == JOptionPane.YES_OPTION;
    }

    private void generateDefaultDbFile(Component parent) throws IOException, SQLException {
        String defaultFilePath = getCurrentDirectory().getAbsolutePath()
                + File.separator + DEFAULT_PREFIX
                + System.currentTimeMillis() + DEFAULT_EXTENSION;
        JOptionPane.showMessageDialog(parent,
                WILL_CREATE_DEFAULT_STORAGE.formatted(defaultFilePath),
                DEFAULT_STORAGE, JOptionPane.WARNING_MESSAGE);
        DbInitializer.createDbFileIfNotExists(new File(defaultFilePath));
    }
}
