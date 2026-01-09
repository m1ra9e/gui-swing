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

import org.slf4j.Logger;

import home.Settings;
import home.db.dao.Dao;
import home.db.init.DbInitializer;
import home.gui.DataActionInGui;
import home.gui.DbOperation;
import home.gui.component.CustomJFileChooserDb;
import home.gui.exception.CreateOpenSaveCancelException;
import home.utils.LogUtils;
import home.utils.ThreadUtil;

/**
 * Action listener for working with databases contained in a file.
 *
 * Supported: SQLite.
 */
public final class CreateOrOpenFileDatabaseActionListener implements ActionListener {

    private final Component parent;
    private final JLabel dbLabel;
    private final Logger log;

    public CreateOrOpenFileDatabaseActionListener(Component parent, JLabel dbLabel, Logger log) {
        this.parent = parent;
        this.dbLabel = dbLabel;
        this.log = log;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        try {
            CustomJFileChooserDb.createAndShowChooser(parent,
                    DbOperation.CREATE_OR_OPEN_FILE_DATABASE);
            createReadDataTable();
        } catch (CreateOpenSaveCancelException e) {
            // to do nothing
            return;
        } catch (IOException e) {
            LogUtils.logAndShowError(log, parent, "Error while create/open DB file.",
                    "Create/Open file error.", e);
        } catch (SQLException e) {
            LogUtils.logAndShowError(log, parent,
                    "Error while read selected DB.\n" + e.getLocalizedMessage(),
                    "Read selected DB error", e);
        }
    }

    private void createReadDataTable() {
        ThreadUtil.runInThread(() -> {
            Thread.currentThread().setName("-> create/read data table");
            try {
                DbInitializer.createTableIfNotExists();
                DataActionInGui.init(Dao.readAll());
                dbLabel.setText(Settings.getDatabase());
            } catch (SQLException e) {
                LogUtils.logAndShowError(log, parent,
                        "Error while create/read data table.\n" + e.getLocalizedMessage(),
                        "Create/read data table error", e);
            }
        });
    }
}
