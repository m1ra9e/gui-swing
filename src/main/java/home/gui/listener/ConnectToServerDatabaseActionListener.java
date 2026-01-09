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
import java.sql.SQLException;

import javax.swing.JLabel;

import org.slf4j.Logger;

import home.Settings;
import home.db.conn.Connector;
import home.db.dao.Dao;
import home.db.init.DbInitializer;
import home.gui.DataActionInGui;
import home.gui.component.dialog.DialogDbConnection;
import home.utils.LogUtils;
import home.utils.Utils;

/**
 * Action listener for working with server databases.
 *
 * Supported: PostgreSql.
 *
 */
public final class ConnectToServerDatabaseActionListener implements ActionListener {

    private final Component parent;
    private final JLabel dbLabel;
    private final Logger log;

    public ConnectToServerDatabaseActionListener(Component parent, JLabel dbLabel, Logger log) {
        this.parent = parent;
        this.dbLabel = dbLabel;
        this.log = log;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Thread.currentThread().setName("-> connect to server database");

        var dialogDbConnction = new DialogDbConnection("Connect to server database", () -> {
            try {
                String dbTypeStr = Settings.getDatabaseType().name();
                Thread.currentThread().setName("-> connect to %s database".formatted(dbTypeStr));
                if (Connector.testCurrentConnection()) {
                    DbInitializer.createTableIfNotExists();
                    DataActionInGui.init(Dao.readAll());
                    dbLabel.setText(Utils.generateDbDescription());
                }
            } catch (SQLException e) {
                LogUtils.logAndShowError(log, parent,
                        "Error while read selected DB.\n" + e.getLocalizedMessage(),
                        "Read selected DB error", e);
            }
        });

        dialogDbConnction.buildDialog();
    }
}
