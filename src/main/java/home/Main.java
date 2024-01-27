package home;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import home.db.DbInitializer;
import home.db.dao.DaoSQLite;
import home.gui.Gui;
import home.gui.component.CustomJFileChooser;
import home.utils.Utils;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        initSettings();
        initGui();

        if (Settings.hasPathToDbFile()) {
            initDb();
        } else {
            try {
                new CustomJFileChooser(null).showCreateOrOpen();
                initDb();
                Gui.getInstance().setDbLabel(Settings.DB_FILE_PATH);
            } catch (IOException e) {
                Utils.logAndShowError(LOG, null, "Error while create/open DB file.",
                        "Create/Open file error.", e);
                System.exit(1);
            }
        }
    }

    private static void initSettings() {
        try {
            Settings.readSettings();
        } catch (Exception e) {
            Utils.logAndShowError(LOG, null, e.getLocalizedMessage(),
                    "Settings initialization error", e);
            System.exit(1);
        }
    }

    private static void initGui() {
        try {
            Gui.getInstance().buildGui();
        } catch (Exception e) {
            Utils.logAndShowError(LOG, null, e.getLocalizedMessage(),
                    "GUI initialization error", e);
            System.exit(1);
        }
    }

    private static void initDb() {
        try {
            DbInitializer.createTableIfNotExists();
            readDataFromDb();
        } catch (Exception e) {
            Utils.logAndShowError(LOG, null, e.getLocalizedMessage(),
                    "DB initialization error", e);
            System.exit(1);
        }
    }

    private static void readDataFromDb() {
        Utils.runInThread(() -> {
            try {
                Storage.getInstance().refresh(DaoSQLite.getInstance().readAll());
            } catch (SQLException e) {
                Utils.logAndShowError(LOG, null,
                        "Error while read data from database: " + e.getLocalizedMessage(),
                        "Data reading error", e);
                System.exit(1);
            }
        });
    }
}
