package home.gui.component;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import home.Settings;
import home.db.DbInitializer;
import home.gui.IGuiConsts;
import home.gui.exception.SaveAsCancelException;
import home.gui.exception.SaveAsToSameFileException;

@SuppressWarnings("serial")
public final class CustomJFileChooserDb extends JFileChooser {

    public static enum ChooserDbOperation {

        CREATE_OR_OPEN(IGuiConsts.CREATE_OR_OPEN),
        SAVE_AS(IGuiConsts.SAVE_AS);

        private String operationText;

        private ChooserDbOperation(String operationText) {
            this.operationText = operationText;
        }

        public String getOperatioText() {
            return operationText;
        }
    }

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

    public static void createAndShowChooser(Component parent, ChooserDbOperation operation)
            throws IOException {
        var fileChooser = new CustomJFileChooserDb();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                EXTENSION_DESCRIPTIONS, EXTENSIONS));
        fileChooser.showChooser(parent, operation);
    }

    private void showChooser(Component parent, ChooserDbOperation operation) throws IOException {
        int chooserState = showDialog(parent, operation.getOperatioText());
        if (JFileChooser.APPROVE_OPTION == chooserState) {
            //// [Create / Open] button pressed
            counterBeforeCreateDefaultFile = 0;
            File file = getSelectedFile();
            file = addExtensionToFileIfNotExists(file);
            if (ChooserDbOperation.SAVE_AS == operation) {
                checkSaveAsFileLocation(file);
            }
            DbInitializer.createDbFileIfNotExists(file);
        } else if (JFileChooser.APPROVE_OPTION != chooserState && ChooserDbOperation.SAVE_AS == operation) {
            //// [Cancel] button pressed while [Save as...]
            throw new SaveAsCancelException("Cancel SaveAs action.");
        } else if (JFileChooser.APPROVE_OPTION != chooserState && !Settings.hasPathToDbFile()) {
            //// [Cancel] button pressed during the action [Create / Open],
            //// while no database was selected before
            ////
            //// Condition of this block is necessary so that when entering the
            //// [Create / Open] menu, you don't need to select database file,
            //// if it already opened.

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

    private void checkSaveAsFileLocation(File file) {
        if (file.exists()) {
            throw new SaveAsToSameFileException("File '"
                    + file.getAbsolutePath() + "' already exists.");
        }
    }

    private void generateDefaultDbFile(Component parent) throws IOException {
        String defaultFilePath = getCurrentDirectory().getAbsolutePath()
                + File.separator + DEFAULT_PREFIX
                + System.currentTimeMillis() + DEFAULT_EXTENSION;
        JOptionPane.showMessageDialog(parent,
                String.format(WILL_CREATE_DEFAULT_STORAGE, defaultFilePath),
                DEFAULT_STORAGE, JOptionPane.WARNING_MESSAGE);
        DbInitializer.createDbFileIfNotExists(new File(defaultFilePath));
    }
}
