package home.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
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
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.Main;
import home.Settings;
import home.Settings.Setting;
import home.Storage;
import home.db.DbInitializer;
import home.db.dao.DaoSQLite;
import home.gui.component.CustomJButton;
import home.gui.component.CustomJFileChooser;
import home.gui.component.CustomJFileChooser.ChooserOperation;
import home.gui.component.CustomJFrame;
import home.gui.component.CustomJPanel;
import home.gui.component.CustomJPanel.PanelType;
import home.gui.component.CustomJTable;
import home.gui.component.dialog.DialogCar;
import home.gui.component.dialog.DialogMotorcycle;
import home.gui.component.dialog.DialogTruck;
import home.gui.exception.SaveAsCancelException;
import home.gui.exception.SaveAsToSameFileException;
import home.models.AbstractVehicle;
import home.utils.Utils;

public enum Gui {

    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(Gui.class);

    private static final int CLICK_COUNT = 2;

    private JLabel dbLabel;

    private JTable table;
    private AbstractTableModel tableModel;
    private JScrollPane tableScrollPane;

    private JButton btnCar;
    private JButton btnTruck;
    private JButton btnMotorcycle;
    private JButton btnDelete;

    private JPanel panelTable;
    private JPanel panelButton;
    private JMenuBar menuBar;
    private JFrame frame;

    public void refreshTable() {
        tableModel.fireTableDataChanged();
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
            if (colorSchema == null) {
                throw new IllegalArgumentException("Incorrect style name : " + style);
            }
            UIManager.setLookAndFeel(colorSchema.getLookAndFeelClassName());
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                Settings.writeSetting(Setting.STYLE,
                        ColorSchema.CROSSPLATFORM.name().toLowerCase(Locale.ROOT));
                Utils.logAndShowError(LOG, frame,
                        "Error while set the system color scheme.\n"
                                + ColorSchema.CROSSPLATFORM.getNameForGui()
                                + " color scheme will be used.\nError: "
                                + e.getMessage(),
                        "System color scheme error", e);
            } catch (Exception ex) {
                JFrame.setDefaultLookAndFeelDecorated(true);
                Utils.logAndShowError(LOG, frame,
                        "Error while set Default color scheme.\nError: " + ex.getMessage(),
                        "System color scheme error", ex);
            }
        }
    }

    private void createTable() {
        dbLabel = new JLabel(Settings.hasPathToDbFile() ? Settings.getDbFilePath()
                : IGuiConsts.CHOOSE_DB_FILE);

        table = CustomJTable.create();
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

        tableModel = (AbstractTableModel) table.getModel();
    }

    private void createButtons() {
        btnCar = CustomJButton.create(IGuiConsts.CAR);
        btnCar.addActionListener(actionEvent -> DialogCaller
                .showObjDialog(frame, DialogCar.class));

        btnTruck = CustomJButton.create(IGuiConsts.TRUCK);
        btnTruck.addActionListener(actionEvent -> DialogCaller
                .showObjDialog(frame, DialogTruck.class));

        btnMotorcycle = CustomJButton.create(IGuiConsts.MOTORCYCLE);
        btnMotorcycle.addActionListener(actionEvent -> DialogCaller
                .showObjDialog(frame, DialogMotorcycle.class));

        btnDelete = CustomJButton.create(IGuiConsts.DELETE);
        btnDelete.addActionListener(actionEvent -> {
            Utils.runInThread("-> delete from storage", () -> {
                List<AbstractVehicle> obsMarkedForDelete = Storage.INSTANCE
                        .getAll().stream()
                        .filter(dataObj -> dataObj.isMarkedForDelete())
                        .collect(Collectors.toList());
                if (!obsMarkedForDelete.isEmpty()) {
                    Storage.INSTANCE.deleteObjects(obsMarkedForDelete);
                    Gui.INSTANCE.refreshTable();
                }
            });
        });
    }

    private void createPanels() {
        panelTable = CustomJPanel.create(PanelType.FRAME_TABLE_PANEL);
        panelTable.add(dbLabel, BorderLayout.NORTH);
        panelTable.add(tableScrollPane, BorderLayout.CENTER);

        panelButton = CustomJPanel.create(PanelType.FRAME_BUTTON_PANEL);
        panelButton.add(btnCar);
        panelButton.add(btnTruck);
        panelButton.add(btnMotorcycle);
        panelButton.add(btnDelete);
    }

    private void createMenu() {
        menuBar = new JMenuBar();

        JMenuItem createOrOpenItem = createMenuItem(IGuiConsts.CREATE_OR_OPEN,
                new CreateOrOpenActionListener(frame, dbLabel));
        JMenuItem saveItem = createMenuItem(IGuiConsts.SAVE,
                new SaveActionListener(frame, dbLabel, false));
        JMenuItem saveAsItem = createMenuItem(IGuiConsts.SAVE_AS,
                new SaveActionListener(frame, dbLabel, true));
        var fileMenu = new JMenu(IGuiConsts.FILE);
        fileMenu.add(createOrOpenItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        menuBar.add(fileMenu);

        menuBar.add(creatStyleMenu());

        JMenuItem aboutItem = createMenuItem(IGuiConsts.ABOUT,
                actionEvent -> JOptionPane.showMessageDialog(
                        frame, IGuiConsts.ABOUT_TEXT.formatted(Main.appVersion),
                        IGuiConsts.ABOUT_TITLE, JOptionPane.INFORMATION_MESSAGE));
        var helpMenu = new JMenu(IGuiConsts.HELP);
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
    }

    private JMenuItem createMenuItem(String name, ActionListener actionListener) {
        var menuItem = new JMenuItem(name);
        menuItem.addActionListener(actionListener);
        return menuItem;
    }

    private JMenu creatStyleMenu() {
        var styleMenu = new JMenu(IGuiConsts.STYLE);
        var checkBoxItems = new ArrayList<JCheckBoxMenuItem>();
        for (ColorSchema colorSchema : ColorSchema.values()) {
            styleMenu.add(createCheckBoxMenuItem(colorSchema, checkBoxItems));
        }
        return styleMenu;
    }

    private JCheckBoxMenuItem createCheckBoxMenuItem(ColorSchema colorSchema,
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

            Settings.writeSetting(Setting.STYLE, selectedItem.getText().toLowerCase(Locale.ROOT));

            setStyle(Settings.getStyle());
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (Exception e) {
            Utils.logAndShowError(LOG, frame,
                    "Error while choosing style.", "Style error", e);
        }
    }

    private void createFrame() {
        frame = CustomJFrame.create(Main.appName);
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(panelTable, BorderLayout.CENTER);
        frame.getContentPane().add(panelButton, BorderLayout.EAST);
        makeFrameVisible();
    }

    /**
     * Creating and displaying a form. When launched via
     * "SwingUtilities.invokeLater(new Runnable(){...}" the frame will be created
     * and displayed after all expected events have been processed, i.e. the frame
     * will be created and displayed when all resources are ready. This is
     * necessary, so that all elements are guaranteed to be displayed in the window
     * (if you do "frame.setVisible(true)" from the main thread, then there is a
     * chance that some element will not be displayed in the window).
     */
    private void makeFrameVisible() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    private static final class CreateOrOpenActionListener implements ActionListener {

        private final Component parent;
        private final JLabel dbLabel;

        public CreateOrOpenActionListener(Component parent, JLabel dbLabel) {
            this.parent = parent;
            this.dbLabel = dbLabel;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Utils.runInThread("-> create or open database", () -> {
                try {
                    CustomJFileChooser.showChooser(parent, ChooserOperation.CREATE_OR_OPEN);
                    DbInitializer.createTableIfNotExists();
                    Storage.INSTANCE.refresh(DaoSQLite.getInstance().readAll());
                    dbLabel.setText(Settings.getDbFilePath());
                } catch (IOException e) {
                    Utils.logAndShowError(LOG, parent, "Error while create/open DB file.",
                            "Create/Open file error.", e);
                } catch (SQLException e) {
                    Utils.logAndShowError(LOG, parent,
                            "Error while read selected DB file.\n" + e.getLocalizedMessage(),
                            "Read selected DB error", e);
                }
            });
        }
    }

    private static final class SaveActionListener implements ActionListener {

        private final Component parent;
        private final JLabel dbLabel;
        private final boolean isSaveAs;

        public SaveActionListener(Component parent, JLabel dbLabel, boolean isSaveAs) {
            this.parent = parent;
            this.dbLabel = dbLabel;
            this.isSaveAs = isSaveAs;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Utils.runInThread("-> save changes to database", () -> {
                try {
                    if (isSaveAs) {
                        try {
                            CustomJFileChooser.showChooser(parent, ChooserOperation.SAVE_AS);
                            DbInitializer.createTableIfNotExists();
                            DaoSQLite.getInstance().saveAs();
                        } catch (SaveAsToSameFileException e) {
                            DaoSQLite.getInstance().saveAllChanges();
                        } catch (SaveAsCancelException e) {
                            // to do nothing
                        }
                    } else {
                        DaoSQLite.getInstance().saveAllChanges();
                    }
                    Storage.INSTANCE.refresh(DaoSQLite.getInstance().readAll());
                    dbLabel.setText(Settings.getDbFilePath());
                    JOptionPane.showMessageDialog(parent, IGuiConsts.SAVE_TEXT,
                            IGuiConsts.SAVE_TITLE, JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    Utils.logAndShowError(LOG, parent, "Error while create/open DB file.",
                            "Create/Open file error.", e);
                } catch (SQLException e) {
                    Utils.logAndShowError(LOG, parent,
                            "Error while work with DB file.\n" + e.getMessage(),
                            "Work with DB error", e);
                }
            });
        }
    }
}
