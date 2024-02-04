package home;

import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.db.DbInitializer;
import home.db.dao.DaoSQLite;
import home.gui.DataActionInGui;
import home.gui.Gui;
import home.gui.component.CustomJFileChooserDb;
import home.gui.component.CustomJFileChooserDb.ChooserDbOperation;
import home.utils.ThreadUtil;
import home.utils.LogUtils;

final class Data {

    private static final Logger LOG = LoggerFactory.getLogger(Data.class);

    static void initDb() {
        if (Settings.hasPathToDbFile()) {
            readDataFromDb();
        } else {
            try {
                CustomJFileChooserDb.createAndShowChooser(null,
                        ChooserDbOperation.CREATE_OR_OPEN);
                readDataFromDb();
                Gui.INSTANCE.setDbLabel(Settings.getDbFilePath());
            } catch (IOException e) {
                throw new IllegalStateException("Error while create/open DB file.", e);
            }
        }
    }

    private static void readDataFromDb() {
        ThreadUtil.runInThread(() -> {
            Thread.currentThread().setName("-> read data from database");
            try {
                DbInitializer.createTableIfNotExists();
                DataActionInGui.init(DaoSQLite.getInstance().readAll());
            } catch (SQLException e) {
                String errorMsg = "Error while read data from database: " + e.getMessage();
                LogUtils.logAndShowError(LOG, null, errorMsg, "Data reading error", e);
                throw new IllegalStateException(errorMsg, e);
            }
        });
    }
}
