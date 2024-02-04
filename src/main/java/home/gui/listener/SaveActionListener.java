package home.gui.listener;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.slf4j.Logger;

import home.Settings;
import home.db.DbInitializer;
import home.db.dao.DaoSQLite;
import home.gui.DataActionInGui;
import home.gui.IGuiConsts;
import home.gui.component.CustomJFileChooserDb;
import home.gui.component.CustomJFileChooserDb.ChooserDbOperation;
import home.gui.exception.SaveAsCancelException;
import home.gui.exception.SaveAsToSameFileException;
import home.utils.LogUtils;
import home.utils.ThreadUtil;

public final class SaveActionListener implements ActionListener {

    private final Component parent;
    private final JLabel dbLabel;
    private final boolean isSaveAs;
    private final Logger log;

    public SaveActionListener(Component parent, JLabel dbLabel,
            boolean isSaveAs, Logger log) {
        this.parent = parent;
        this.dbLabel = dbLabel;
        this.isSaveAs = isSaveAs;
        this.log = log;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ThreadUtil.runInThread(() -> {
            Thread.currentThread().setName("-> save changes to database");
            try {
                if (isSaveAs) {
                    try {
                        CustomJFileChooserDb.createAndShowChooser(parent,
                                ChooserDbOperation.SAVE_AS);
                        DbInitializer.createTableIfNotExists();
                        DaoSQLite.getInstance().saveAs();
                    } catch (SaveAsToSameFileException e) {
                        DaoSQLite.getInstance().saveAllChanges();
                    } catch (SaveAsCancelException e) {
                        // to do nothing
                        return;
                    }
                } else {
                    DaoSQLite.getInstance().saveAllChanges();
                }

                DataActionInGui.init(DaoSQLite.getInstance().readAll());
                dbLabel.setText(Settings.getDbFilePath());
                JOptionPane.showMessageDialog(parent, IGuiConsts.SAVE_TEXT,
                        IGuiConsts.SAVE_TITLE, JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                LogUtils.logAndShowError(log, parent, "Error while create/open DB file.",
                        "Create/Open file error.", e);
            } catch (SQLException e) {
                LogUtils.logAndShowError(log, parent,
                        "Error while work with DB file.\n" + e.getMessage(),
                        "Work with DB error", e);
            }
        });
    }
}
