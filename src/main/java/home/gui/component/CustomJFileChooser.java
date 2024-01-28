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
import home.gui.exception.SaveAsCancelException;
import home.gui.exception.SaveAsToSameFileException;

@SuppressWarnings("serial")
public final class CustomJFileChooser extends JFileChooser {

    public static enum ChooserOperation {

        CREATE_OR_OPEN("Create / Open"),
        SAVE_AS("Save as ...");

        private String operationText;

        private ChooserOperation(String operationText) {
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

    private final Component parent;
    private final ChooserOperation operation;

    private CustomJFileChooser(Component parent, ChooserOperation operation) {
        super(APPLICATION_DIR);
        this.parent = parent;
        this.operation = operation;
    }

    public static void showChooser(Component parent, ChooserOperation operation)
            throws IOException {
        var fileChooser = new CustomJFileChooser(parent, operation);
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                EXTENSION_DESCRIPTIONS, EXTENSIONS));
        fileChooser.showChooser();
    }

    private void showChooser() throws IOException {
        int chooserState = showDialog(parent, operation.getOperatioText());
        if (JFileChooser.APPROVE_OPTION == chooserState) {
            counterBeforeCreateDefaultFile = 0;
            File file = getSelectedFile();
            file = addExtensionToFileIfNotExists(file);
            if (ChooserOperation.SAVE_AS == operation) {
                checkSaveAsFileLocation(file);
            }
            DbInitializer.createDbFileIfNotExists(file);
        } else if (JFileChooser.APPROVE_OPTION != chooserState && ChooserOperation.SAVE_AS == operation) {
            throw new SaveAsCancelException("Cancel SaveAs action.");
        } else if (JFileChooser.APPROVE_OPTION != chooserState && !Settings.hasPathToDbFile()) {
            //// Condition of this block is necessary so that when entering the
            //// [Create / Open] menu, you don't need to select database file,
            //// if it already opened.

            if (MAX_TRY_COUNT_BEFORE_CREATE_DEFAULT_FILE == counterBeforeCreateDefaultFile) {
                String defaultFilePath = getCurrentDirectory().getAbsolutePath()
                        + File.separator + DEFAULT_PREFIX
                        + System.currentTimeMillis() + DEFAULT_EXTENSION;
                JOptionPane.showMessageDialog(parent,
                        String.format(WILL_CREATE_DEFAULT_STORAGE, defaultFilePath),
                        DEFAULT_STORAGE, JOptionPane.WARNING_MESSAGE);
                DbInitializer.createDbFileIfNotExists(new File(defaultFilePath));
                return;
            }
            counterBeforeCreateDefaultFile++;
            JOptionPane.showMessageDialog(parent, TYPE_NAME_OR_CHOOSE_DB_FILE,
                    CHOOSE_STORAGE, JOptionPane.WARNING_MESSAGE);
            showChooser();
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
}
