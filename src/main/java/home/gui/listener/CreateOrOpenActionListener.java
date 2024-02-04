package home.gui.listener;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JLabel;

import org.slf4j.Logger;

import home.Settings;
import home.db.DbInitializer;
import home.db.dao.DaoSQLite;
import home.gui.DataActionInGui;
import home.gui.component.CustomJFileChooserDb;
import home.gui.component.CustomJFileChooserDb.ChooserDbOperation;
import home.utils.ThreadUtil;
import home.utils.LogUtils;

public final class CreateOrOpenActionListener implements ActionListener {

    private final Component parent;
    private final JLabel dbLabel;
    private final Logger log;

    public CreateOrOpenActionListener(Component parent, JLabel dbLabel, Logger log) {
        this.parent = parent;
        this.dbLabel = dbLabel;
        this.log = log;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ThreadUtil.runInThread(() -> {
            Thread.currentThread().setName("-> create or open database");
            try {
                CustomJFileChooserDb.createAndShowChooser(parent,
                        ChooserDbOperation.CREATE_OR_OPEN);
                DbInitializer.createTableIfNotExists();
                DataActionInGui.init(DaoSQLite.getInstance().readAll());
                dbLabel.setText(Settings.getDbFilePath());
            } catch (IOException e) {
                LogUtils.logAndShowError(log, parent, "Error while create/open DB file.",
                        "Create/Open file error.", e);
            } catch (SQLException e) {
                LogUtils.logAndShowError(log, parent,
                        "Error while read selected DB file.\n" + e.getLocalizedMessage(),
                        "Read selected DB error", e);
            }
        });
    }
}
