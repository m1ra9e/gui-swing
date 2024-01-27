package home.gui.component;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import home.db.DbInitializer;

@SuppressWarnings("serial")
public class CustomJFileChooser extends JFileChooser {

    private static final File APPLICATION_DIR = new File(".");
    private static final String EXTENSION_DESCRIPTIONS = "SQLite DB (*.db, *.sqlite, *.sqlite3)";
    private static final String[] EXTENSIONS = { "db", "sqlite", "sqlite3" };

    private static final String CREATE_OR_OPEN = "Create or Open";
    private static final String CHOOSE_STORAGE = "Choose storage";
    private static final String TYPE_NAME_OR_CHOOSE_DB_FILE = "Type a new file"
            + " name or choose already existed SQLite DB file.";

    private static final int MAX_REJECTIONS_COUNT = 3;
    private int selectionRejectionCounter;

    private final Component parent;

    public CustomJFileChooser(Component parent) {
        super(APPLICATION_DIR);
        this.parent = parent;
        setFileFilter(new FileNameExtensionFilter(
                EXTENSION_DESCRIPTIONS, EXTENSIONS));
    }

    public void showCreateOrOpen() throws IOException {
        if (JFileChooser.APPROVE_OPTION == showDialog(parent, CREATE_OR_OPEN)) {
            selectionRejectionCounter = 0;
            File file = getSelectedFile();
            checkDbFileExtension(file);
            DbInitializer.createDbFileIfNotExists(file);
        } else {
            JOptionPane.showMessageDialog(parent, TYPE_NAME_OR_CHOOSE_DB_FILE,
                    CHOOSE_STORAGE, JOptionPane.WARNING_MESSAGE);
            selectionRejectionCounter++;
            if (MAX_REJECTIONS_COUNT == selectionRejectionCounter) {
                System.exit(1);
            }
            showCreateOrOpen();
        }
    }

    private void checkDbFileExtension(File file) throws IOException {
        if (Arrays.stream(EXTENSIONS)
                .anyMatch(extension -> file.getName().contains('.' + extension))) {
            return;
        }
        throw new IOException("The file does not contain one of"
                + " the extensions: " + String.join(", ", EXTENSIONS));
    }
}
