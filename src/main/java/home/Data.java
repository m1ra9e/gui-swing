/*******************************************************************************
 * Copyright 2021-2025 Lenar Shamsutdinov
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
package home;

import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.db.conn.Connector;
import home.db.dao.Dao;
import home.db.init.DbInitializer;
import home.gui.DataActionInGui;
import home.gui.Gui;
import home.gui.GuiConst;
import home.utils.LogUtils;
import home.utils.ThreadUtil;
import home.utils.Utils;

final class Data {

    private static final Logger LOG = LoggerFactory.getLogger(Data.class);

    static void initDb() {
        if (!Settings.hasDatabase()) {
            return;
        }

        int dialogContinuePreviousDbResult = JOptionPane.showConfirmDialog(null,
                GuiConst.PREVIOUS_DATABASE_TEXT.formatted(Utils.generateDbDescription()),
                GuiConst.PREVIOUS_DATABASE_TITLE, JOptionPane.YES_NO_OPTION);
        if (dialogContinuePreviousDbResult == JOptionPane.YES_OPTION) {
            readDataFromDb();
            return;
        }

        try {
            Connector.resetConnectionDataAndSettings();
            Gui.INSTANCE.setDbLabel(GuiConst.DATABASE_NOT_SELECTED);
            JOptionPane.showMessageDialog(null, GuiConst.REMOVED_PREVIOUS_CONNECTION_TEXT,
                    GuiConst.REMOVED_PREVIOUS_CONNECTION_TITLE, JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            throw LogUtils.logAndCreateIllegalStateException(
                    "Error while removing connection to previous database.", LOG, e);
        }
    }

    private static void readDataFromDb() {
        ThreadUtil.runInThread(() -> {
            Thread.currentThread().setName("-> read data from database");
            try {
                DbInitializer.createTableIfNotExists();
                DataActionInGui.init(Dao.readAll());
            } catch (SQLException e) {
                String errorMsg = "Error while read data from database: " + e.getMessage();
                LogUtils.logAndShowError(LOG, null, errorMsg, "Data reading error", e);
                throw new IllegalStateException(errorMsg, e);
            }
        });
    }
}
