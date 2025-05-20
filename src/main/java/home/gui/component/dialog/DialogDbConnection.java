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
package home.gui.component.dialog;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.EnumSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import home.Const;
import home.Settings;
import home.Settings.Setting;
import home.db.DbType;
import home.db.conn.Connector;
import home.gui.GuiConst;
import home.gui.component.CustomJButton;
import home.gui.component.CustomJCheckBox;
import home.gui.component.CustomJComboBox;
import home.gui.component.CustomJLabel;
import home.gui.component.CustomJPanel;
import home.gui.component.CustomJPanel.PanelType;
import home.gui.component.CustomJPasswordField;
import home.gui.component.CustomJTextField;
import home.utils.ThreadUtil;

@SuppressWarnings("serial")
public final class DialogDbConnection extends AbstractCustomJDialog {

    private enum ProcessingType {

        SAVE_AND_CONNECT(GuiConst.SAVE_AND_CONNECT),
        TEST(GuiConst.TEST_CONNECTION);

        private final String title;

        private ProcessingType(String title) {
            this.title = title;
        }

        private String getTitle() {
            return title;
        }
    }

    private static final int OBJ_DIALOG_WIDTH = 450;
    private static final int OBJ_DIALOG_HEIGHT = 600;

    private static final int TEXT_FIELD_COLUMN_NUMBERS = 9;

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final String DEFAULT_POST = "5432";

    private JLabel lblHost;
    private JLabel lblPort;
    private JLabel lblDbName;
    private JLabel lblUser;
    private JLabel lblPass;
    private JLabel lblDbType;

    private JTextField tfHost;
    private JTextField tfPort;
    private JTextField tfDbName;
    private JTextField tfUser;
    private CustomJPasswordField tfPass;
    private JCheckBox cbShowPass;
    private JComboBox<String> cbDbType;

    private JButton btnTestConnection;
    private JButton btnSaveConnection;
    private JButton btnCancel;

    protected JPanel panelTextFields;
    private JPanel panelButtons;

    private final Runnable additionalActionsOnSave;
    private WindowAdapter additionalActionsOnSaveListener;

    public DialogDbConnection(String title, Runnable additionalActionsOnSave) {
        super(title, OBJ_DIALOG_WIDTH, OBJ_DIALOG_HEIGHT);
        this.additionalActionsOnSave = additionalActionsOnSave;
    }

    public void buildDialog() {
        init();

        createDataComponents();
        createButtons();
        createPanels();
        createDialog();

        fillDataComponents();
        addListeners();
    }

    private void createDataComponents() {
        lblHost = CustomJLabel.create(GuiConst.HOST);
        lblPort = CustomJLabel.create(GuiConst.PORT);
        lblDbName = CustomJLabel.create(GuiConst.DB_NAME);
        lblUser = CustomJLabel.create(GuiConst.USER);
        lblPass = CustomJLabel.create(GuiConst.PASS);
        lblDbType = CustomJLabel.create(GuiConst.DB_TYPE);

        tfHost = CustomJTextField.create(TEXT_FIELD_COLUMN_NUMBERS);
        tfPort = CustomJTextField.create(TEXT_FIELD_COLUMN_NUMBERS);
        tfDbName = CustomJTextField.create(TEXT_FIELD_COLUMN_NUMBERS);
        tfUser = CustomJTextField.create(TEXT_FIELD_COLUMN_NUMBERS);
        tfPass = CustomJPasswordField.create(TEXT_FIELD_COLUMN_NUMBERS);
        cbShowPass = CustomJCheckBox.create(GuiConst.SHOW_PASS,
                itemEvent -> tfPass.showPassword(itemEvent.getStateChange() == ItemEvent.SELECTED));
        cbDbType = CustomJComboBox.create(EnumSet.of(DbType.PostgreSQL));
    }

    private void createButtons() {
        btnTestConnection = CustomJButton.create(ProcessingType.TEST.getTitle());
        btnTestConnection.addActionListener(actionEvent -> processConnSettings(ProcessingType.TEST));
        btnSaveConnection = CustomJButton.create(ProcessingType.SAVE_AND_CONNECT.getTitle());
        btnSaveConnection.addActionListener(actionEvent -> {
            if (processConnSettings(ProcessingType.SAVE_AND_CONNECT)) {
                dispose();
            }
        });
        btnCancel = CustomJButton.create(GuiConst.CANCEL);
        btnCancel.addActionListener(actionEvent -> closeDialogWithoutAdditionalActions());
    }

    private boolean processConnSettings(ProcessingType settingsProcessingType) {
        try {
            String host = tfHost.getText().strip();
            String port = tfPort.getText().strip();
            String dbName = tfDbName.getText().strip();
            String user = tfUser.getText().strip();
            String pass = new String(tfPass.getPassword());
            String dbTypeStr = cbDbType.getItemAt(cbDbType.getSelectedIndex());

            checkFields(host, port, dbName, user, pass, dbTypeStr);

            DbType dbType = DbType.getDbType(dbTypeStr);

            switch (settingsProcessingType) {
                case SAVE_AND_CONNECT -> {
                    Connector.resetConnectionDataAndSettings();
                    Settings.saveDbConnSettings(host, port, dbName, user, pass, dbType.name());
                }
                case TEST -> {
                    if (Connector.testConnection(host, Integer.parseInt(port), dbName, user, pass, dbType)) {
                        JOptionPane.showMessageDialog(null, GuiConst.CONNECTION_TEST_SUCCESSFUL_TEXT,
                                GuiConst.CONNECTION_TEST_SUCCESSFUL_TITLE, JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }

            return true;
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Connection error:\n%s".formatted(e.getMessage()),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void checkFields(String host, String port, String dbName, String user,
            String pass, String dbType) throws SQLException {
        if (hasEmptyFields(host, port, dbName, user, pass, dbType)) {
            throw new SQLException("Not all fields are filled.");
        }

        try {
            Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw new SQLException("Incorrect value of parameter %s: %s"
                    .formatted(Setting.PORT, port));
        }
    }

    public boolean hasEmptyFields() {
        return hasEmptyFields(tfHost.getText().strip(), tfPort.getText().strip(),
                tfDbName.getText().strip(), tfUser.getText().strip(),
                new String(tfPass.getPassword()).strip(),
                cbDbType.getItemAt(cbDbType.getSelectedIndex()));
    }

    private boolean hasEmptyFields(String host, String port, String dbName,
            String user, String pass, String dbType) {
        return host.isBlank() || port.isBlank() || dbName.isBlank()
                || user.isBlank() || pass.isBlank() || dbType.isBlank();
    }

    private void closeDialogWithoutAdditionalActions() {
        removeWindowListener(additionalActionsOnSaveListener);
        dispose();
    }

    private void createPanels() {
        panelTextFields = CustomJPanel.create(PanelType.CONNECTION_DIALOG_TEXT_FIELDS_PANEL);
        panelTextFields.add(lblHost);
        panelTextFields.add(tfHost);
        panelTextFields.add(lblPort);
        panelTextFields.add(tfPort);
        panelTextFields.add(lblDbName);
        panelTextFields.add(tfDbName);
        panelTextFields.add(lblUser);
        panelTextFields.add(tfUser);
        panelTextFields.add(lblPass);
        panelTextFields.add(tfPass);
        panelTextFields.add(cbShowPass);
        panelTextFields.add(lblDbType);
        panelTextFields.add(cbDbType);

        panelButtons = CustomJPanel.create(PanelType.CONNECTION_DIALOG_BUTTON_PANEL);
        panelButtons.add(btnTestConnection);
        panelButtons.add(btnSaveConnection);
        panelButtons.add(btnCancel);
    }

    private void createDialog() {
        getContentPane().add(panelTextFields, BorderLayout.CENTER);
        getContentPane().add(panelButtons, BorderLayout.SOUTH);
        makeDialogVisible();
    }

    private void fillDataComponents() {
        try {
            DbType dbType = Settings.getDatabaseType();
            if (dbType == DbType.PostgreSQL) {
                String host = Settings.getHost();
                String dbName = Settings.getDatabase();
                String user = Settings.getUser();
                String pass = Settings.getPassword();
                if (!host.isBlank() && !dbName.isBlank() && !user.isBlank() && !pass.isBlank()) {
                    tfHost.setText(host.strip());
                    tfDbName.setText(dbName.strip());
                    tfUser.setText(user.strip());
                    tfPass.setText(pass.strip());
                    cbDbType.setSelectedItem(dbType.name());

                    String port;
                    try {
                        port = Integer.toString(Settings.getPort());
                    } catch (Exception e) {
                        port = Const.EMPTY_STRING;
                    }
                    tfPort.setText(port);
                }
            } else {
                fillDataComponentsWithDefaultValues();
            }
        } catch (SQLException e) {
            fillDataComponentsWithDefaultValues();
        }
    }

    private void fillDataComponentsWithDefaultValues() {
        tfHost.setText(DEFAULT_HOST);
        tfPort.setText(DEFAULT_POST);
        cbDbType.setSelectedItem(DbType.PostgreSQL.name());
    }

    private void addListeners() {
        additionalActionsOnSaveListener = new AdditionalActionsOnSaveListener(additionalActionsOnSave);
        addWindowListener(additionalActionsOnSaveListener);
        addWindowListener(new NoAdditinalActionsOnEscapeListener());
    }

    @Override
    protected void actionOnHotKeyForClose() {
        closeDialogWithoutAdditionalActions();
    }

    private static final class AdditionalActionsOnSaveListener extends WindowAdapter {

        private final Runnable additionalActionsOnSave;

        private AdditionalActionsOnSaveListener(Runnable additionalActionsOnSave) {
            this.additionalActionsOnSave = additionalActionsOnSave;
        }

        @Override
        public void windowClosed(WindowEvent event) {
            ThreadUtil.runInThread(additionalActionsOnSave);
            super.windowClosed(event);
        }
    }

    private static final class NoAdditinalActionsOnEscapeListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent event) {
            ((DialogDbConnection) event.getWindow()).closeDialogWithoutAdditionalActions();
        }
    }
}
