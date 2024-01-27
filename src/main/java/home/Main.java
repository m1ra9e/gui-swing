package home;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import home.db.DbInitializer;
import home.db.dao.DaoSQLite;
import home.gui.Gui;
import home.utils.Utils;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        initDb();
        initGui();
        readDataFromDb();
    }

    private static void initDb() {
        try {
            DbInitializer.createTableIfNotExists();
        } catch (Exception e) {
            LOG.error(e);
            JOptionPane.showMessageDialog(null,
                    e.getLocalizedMessage(),
                    "DB initialization error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private static void initGui() {
        try {
            Gui.getInstance().buildGui();
        } catch (Exception e) {
            LOG.error(e);
            JOptionPane.showMessageDialog(null,
                    e.getLocalizedMessage(),
                    "GUI initialization error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private static void readDataFromDb() {
        Utils.runInThread(() -> {
            try {
                Storage.getInstance().refresh(DaoSQLite.getInstance().readAll());
            } catch (SQLException e) {
                LOG.error(e);
                JOptionPane.showMessageDialog(null,
                        "Error while read data from database: "
                                + e.getLocalizedMessage(),
                        "Data reading error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
