/*******************************************************************************
 * Copyright 2021-2024 Lenar Shamsutdinov
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
package home.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.Main;
import home.Settings;
import home.Storage;
import home.gui.component.CustomJButton;
import home.gui.component.CustomJFileChooserImpExp.DataFormat;
import home.gui.component.CustomJFrame;
import home.gui.component.CustomJLabel;
import home.gui.component.CustomJPanel;
import home.gui.component.CustomJPanel.PanelType;
import home.gui.component.CustomJTable;
import home.gui.component.CustomJTableDataModel;
import home.gui.component.dialog.DialogCar;
import home.gui.component.dialog.DialogMotorcycle;
import home.gui.component.dialog.DialogTruck;
import home.gui.listener.ConnectToServerDatabaseActionListener;
import home.gui.listener.CreateOrOpenFileDatabaseActionListener;
import home.gui.listener.ExportImportActionListener;
import home.gui.listener.SaveActionListener;
import home.model.AbstractVehicle;
import home.utils.LogUtils;
import home.utils.ThreadUtil;
import home.utils.Utils;

public enum Gui {

    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(Gui.class);

    private static final int CLICK_COUNT = 2;

    private JLabel dbLabel;

    private CustomJTable table;
    private AbstractTableModel tableDataModel;
    private JScrollPane tableScrollPane;

    private JButton btnCar;
    private JButton btnTruck;
    private JButton btnMotorcycle;
    private JButton btnDelete;

    private JPanel panelTable;
    private JPanel panelButton;
    private JMenuBar menuBar;
    private CustomJFrame frame;

    public void refreshTable() {
        tableDataModel.fireTableDataChanged();
    }

    public void setDbLabel(String label) {
        dbLabel.setText(label);
    }

    public void buildGui() {
        setStyle(Settings.getStyle());

        createTable();
        createButtons();
        createPanels();
        createMenu();
        createFrame();
    }

    private void setStyle(String style) {
        try {
            ColorSchema colorSchema = ColorSchema.getColorSchema(style);
            UIManager.setLookAndFeel(colorSchema.getLookAndFeelClassName());
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                Settings.saveStyle(ColorSchema.CROSSPLATFORM.name().toLowerCase(Locale.ROOT));
                LogUtils.logAndShowError(LOG, frame, """
                        Error while set the system color scheme.
                        %s color scheme will be used.
                        Error: %s"""
                        .formatted(ColorSchema.CROSSPLATFORM.getNameForGui(), e.getMessage()),
                        "System color scheme error", e);
            } catch (Exception ex) {
                JFrame.setDefaultLookAndFeelDecorated(true);
                LogUtils.logAndShowError(LOG, frame,
                        "Error while set Default color scheme.\nError: " + ex.getMessage(),
                        "System color scheme error", ex);
            }
        }
    }

    private void createTable() {
        dbLabel = CustomJLabel.createSmall(Utils.generateDbDescription());

        tableDataModel = new CustomJTableDataModel(Storage.INSTANCE.getAll());

        table = CustomJTable.create(tableDataModel, Settings.isAutoResizeTableWidth());
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                if (CLICK_COUNT == mouseEvent.getClickCount()) {
                    int selectedTableRow = table.getSelectedRow();
                    DialogCaller.showObjDialog(frame,
                            Storage.INSTANCE.get(selectedTableRow), selectedTableRow);
                }
            }
        });

        tableScrollPane = new JScrollPane(table);
    }

    private void createButtons() {
        btnCar = CustomJButton.create(GuiConst.CAR);
        btnCar.addActionListener(actionEvent -> DialogCaller
                .showObjDialog(frame, DialogCar.class));

        btnTruck = CustomJButton.create(GuiConst.TRUCK);
        btnTruck.addActionListener(actionEvent -> DialogCaller
                .showObjDialog(frame, DialogTruck.class));

        btnMotorcycle = CustomJButton.create(GuiConst.MOTORCYCLE);
        btnMotorcycle.addActionListener(actionEvent -> DialogCaller
                .showObjDialog(frame, DialogMotorcycle.class));

        btnDelete = CustomJButton.create(GuiConst.DELETE);
        btnDelete.addActionListener(actionEvent -> {
            ThreadUtil.runInThread(() -> {
                Thread.currentThread().setName("-> delete from storage");
                List<AbstractVehicle> objsMarkedForDelete = Storage.INSTANCE
                        .getAll().stream()
                        .filter(dataObj -> dataObj.isMarkedForDelete())
                        .collect(Collectors.toList());
                if (!objsMarkedForDelete.isEmpty()) {
                    DataActionInGui.delete(objsMarkedForDelete);
                }
            });
        });
    }

    private void createPanels() {
        panelTable = CustomJPanel.create(PanelType.MAIN_FRAME_TABLE_PANEL);
        panelTable.add(dbLabel, BorderLayout.NORTH);
        panelTable.add(tableScrollPane, BorderLayout.CENTER);

        panelButton = CustomJPanel.create(PanelType.MAIN_FRAME_BUTTON_PANEL);
        panelButton.add(btnCar);
        panelButton.add(btnTruck);
        panelButton.add(btnMotorcycle);
        panelButton.add(btnDelete);
    }

    private void createMenu() {
        menuBar = new JMenuBar();

        JMenuItem createOrOpenFileDatabaseItem = createMenuItem(
                DbOperation.CREATE_OR_OPEN_FILE_DATABASE.getOperatioText(),
                new CreateOrOpenFileDatabaseActionListener(frame, dbLabel, LOG));
        JMenuItem connectToServerDatabaseItem = createMenuItem(
                DbOperation.CONNECT_TO_SERVER_DATABASE.getOperatioText(),
                new ConnectToServerDatabaseActionListener(frame, dbLabel, LOG));
        JMenuItem saveItem = createMenuItem(
                DbOperation.SAVE.getOperatioText(),
                new SaveActionListener(frame, dbLabel, false, LOG));
        JMenuItem saveAsItem = createMenuItem(
                DbOperation.SAVE_AS.getOperatioText(),
                new SaveActionListener(frame, dbLabel, true, LOG));
        JMenu importItem = createImportExportDropdownMenu(true);
        JMenu exportItem = createImportExportDropdownMenu(false);
        var fileMenu = new JMenu(GuiConst.FILE);
        fileMenu.add(createOrOpenFileDatabaseItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(connectToServerDatabaseItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(importItem);
        fileMenu.add(exportItem);
        menuBar.add(fileMenu);

        menuBar.add(creatStyleMenu());

        JMenuItem aboutItem = createMenuItem(GuiConst.ABOUT,
                actionEvent -> JOptionPane.showMessageDialog(
                        frame, GuiConst.ABOUT_TEXT.formatted(Main.appVersion),
                        GuiConst.ABOUT_TITLE, JOptionPane.INFORMATION_MESSAGE));
        var helpMenu = new JMenu(GuiConst.HELP);
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
    }

    private JMenuItem createMenuItem(String name, ActionListener actionListener) {
        var menuItem = new JMenuItem(name);
        menuItem.addActionListener(actionListener);
        return menuItem;
    }

    private JMenu createImportExportDropdownMenu(boolean isImport) {
        var dropdownMenu = new JMenu(isImport ? GuiConst.IMPORT_FROM : GuiConst.EXPORT_TO);
        for (DataFormat dataFormat : DataFormat.values()) {
            dropdownMenu.add(createMenuItem(dataFormat.getExtension(),
                    new ExportImportActionListener(dataFormat, isImport, frame, LOG)));
        }
        return dropdownMenu;
    }

    private JMenu creatStyleMenu() {
        var styleMenu = new JMenu(GuiConst.STYLE);

        styleMenu.add(createCheckBoxResizeTblMenuItem());
        styleMenu.add(new JSeparator());

        var checkBoxItems = new ArrayList<JCheckBoxMenuItem>();
        for (ColorSchema colorSchema : ColorSchema.values()) {
            styleMenu.add(createCheckBoxColorSchemaMenuItem(colorSchema, checkBoxItems));
        }

        return styleMenu;
    }

    private JCheckBoxMenuItem createCheckBoxColorSchemaMenuItem(ColorSchema colorSchema,
            List<JCheckBoxMenuItem> checkBoxItems) {
        var checkBoxMenuItem = new JCheckBoxMenuItem(colorSchema.getNameForGui());
        checkBoxMenuItem.setSelected(
                colorSchema.name().equalsIgnoreCase(Settings.getStyle()));

        checkBoxItems.add(checkBoxMenuItem);

        checkBoxMenuItem.addActionListener(
                actionEvent -> styleSelectAction(actionEvent, checkBoxItems));

        return checkBoxMenuItem;
    }

    private void styleSelectAction(ActionEvent actionEvent,
            List<JCheckBoxMenuItem> checkBoxItems) {
        try {
            checkBoxItems.stream().forEach(item -> item.setSelected(false));

            var selectedItem = (JCheckBoxMenuItem) actionEvent.getSource();
            selectedItem.setSelected(true);

            Settings.saveStyle(selectedItem.getText().toLowerCase(Locale.ROOT));

            setStyle(Settings.getStyle());
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (Exception e) {
            LogUtils.logAndShowError(LOG, frame,
                    "Error while choosing style.", "Style error", e);
        }
    }

    private JCheckBoxMenuItem createCheckBoxResizeTblMenuItem() {
        var checkBoxMenuItem = new JCheckBoxMenuItem(GuiConst.AUTO_RESIZE_TABLE_WIDTH);
        checkBoxMenuItem.setSelected(Settings.isAutoResizeTableWidth());

        checkBoxMenuItem.addActionListener(actionEvent -> {
            try {
                boolean isAutoResizeTableWidthCurrent = Settings.isAutoResizeTableWidth();
                boolean isAutoResizeTableWidthNew = !isAutoResizeTableWidthCurrent;

                Settings.saveAutoResizeTableWidth(isAutoResizeTableWidthNew);

                checkBoxMenuItem.setSelected(isAutoResizeTableWidthNew);
                table.setAutoResize(isAutoResizeTableWidthNew);

                SwingUtilities.updateComponentTreeUI(frame);
            } catch (Exception e) {
                LogUtils.logAndShowError(LOG, frame,
                        "Error while changing auto resizing mode of table.", "Style error", e);
            }
        });

        return checkBoxMenuItem;
    }

    private void createFrame() {
        frame = CustomJFrame.create(Main.appName);
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(panelTable, BorderLayout.CENTER);
        frame.getContentPane().add(panelButton, BorderLayout.EAST);
        frame.makeFrameVisible();
    }
}
