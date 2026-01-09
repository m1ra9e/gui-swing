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
import home.db.dao.Dao;
import home.db.init.DbInitializer;
import home.gui.DataActionInGui;
import home.gui.DbOperation;
import home.gui.GuiConst;
import home.gui.component.CustomJFileChooserDb;
import home.gui.exception.CreateOpenSaveCancelException;
import home.gui.exception.SaveAsCancelException;
import home.gui.exception.SaveToAlreadyExistsFileException;
import home.utils.LogUtils;
import home.utils.ThreadUtil;
import home.utils.Utils;

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
        try {
            if (isSaveAs) {
                try {
                    CustomJFileChooserDb.createAndShowChooser(parent, DbOperation.SAVE_AS);
                } catch (SaveToAlreadyExistsFileException e) {
                    JOptionPane.showMessageDialog(parent, GuiConst.ALREADY_EXISTS_TEXT,
                            GuiConst.ALREADY_EXISTS_TITLE, JOptionPane.ERROR_MESSAGE);
                    return;
                } catch (SaveAsCancelException e) {
                    // to do nothing
                    return;
                }
            } else {
                try {
                    if (!Settings.hasDatabase()) {
                        CustomJFileChooserDb.createAndShowChooser(parent, DbOperation.SAVE);
                        DbInitializer.createTableIfNotExists();
                    }
                } catch (SaveToAlreadyExistsFileException e) {
                    JOptionPane.showMessageDialog(parent, GuiConst.ALREADY_EXISTS_TEXT,
                            GuiConst.ALREADY_EXISTS_TITLE, JOptionPane.ERROR_MESSAGE);
                    return;
                } catch (CreateOpenSaveCancelException e) {
                    // to do nothing
                    return;
                }
            }

            saveChangesToDb();
        } catch (IOException e) {
            LogUtils.logAndShowError(log, parent, "Error while create/open DB file.",
                    "Create/Open file error.", e);
        } catch (SQLException e) {
            LogUtils.logAndShowError(log, parent,
                    "Error while work with DB.\n" + e.getMessage(),
                    "Work with DB error", e);
        }
    }

    private void saveChangesToDb() {
        ThreadUtil.runInThread(() -> {
            Thread.currentThread().setName("-> save changes to database");

            try {
                if (isSaveAs) {
                    DbInitializer.createTableIfNotExists();
                    Dao.saveAs();
                } else {
                    Dao.saveAllChanges();
                }

                DataActionInGui.init(Dao.readAll());
                dbLabel.setText(Utils.generateDbDescription());

                JOptionPane.showMessageDialog(parent, GuiConst.SAVE_TEXT,
                        GuiConst.SAVE_TITLE, JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                LogUtils.logAndShowError(log, parent,
                        "Error while work with DB.\n" + e.getMessage(),
                        "Work with DB error", e);
            }
        });
    }
}
