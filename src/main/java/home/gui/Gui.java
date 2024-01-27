package home.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import home.Settings;
import home.Storage;
import home.db.DbInitializer;
import home.db.dao.DaoSQLite;
import home.gui.component.CustomJButton;
import home.gui.component.CustomJFileChooser;
import home.gui.component.CustomJFrame;
import home.gui.component.CustomJPanel;
import home.gui.component.CustomJPanel.PanelType;
import home.gui.component.CustomJTable;
import home.gui.component.dialog.DialogCar;
import home.gui.component.dialog.DialogMotorcycle;
import home.gui.component.dialog.DialogTruck;
import home.utils.Utils;

public class Gui {

    private static final Logger LOG = Logger.getLogger(Gui.class);

    private static final int CLICK_COUNT = 2;

    private static Gui instance;

    private JLabel dbLabel;

    private JTable table;
    private AbstractTableModel tableModel;
    private JScrollPane tableScrollPane;

    private CustomJButton btnCar;
    private CustomJButton btnTruck;
    private CustomJButton btnMotorcycle;
    private CustomJButton btnDelete;

    private CustomJPanel panelTable;
    private CustomJPanel panelButton;
    private JMenuBar menuBar;
    private CustomJFrame frame;

    private Gui() {
    }

    public static Gui getInstance() {
        if (instance == null) {
            instance = new Gui();
        }
        return instance;
    }

    public void refreshTable() {
        tableModel.fireTableDataChanged();
    }

    public void setDbLabel(String label) {
        dbLabel.setText(label);
    }

    public void buildGui() throws Exception {
        setStyle(Settings.STYLE);

        createTable();
        createButtons();
        createPanels();
        createMenu();
        createFrame();

        madeGuiVisible();
    }

    private void setStyle(String style) {
        // JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel(style.equalsIgnoreCase(GuiConsts.SYSTEM)
                    ? UIManager.getSystemLookAndFeelClassName()
                    : UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            Utils.logAndShowError(LOG, frame,
                    "Error while set the system color scheme.\n"
                            + "Default color scheme will be used.\n"
                            + e.getLocalizedMessage(),
                    "System color scheme error", e);
        }
    }

    private void createTable() {
        dbLabel = new JLabel(Settings.hasPathToDbFile() ? Settings.DB_FILE_PATH
                : GuiConsts.CHOOSE_DB_FILE);

        table = new CustomJTable();
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                if (CLICK_COUNT == mouseEvent.getClickCount()) {
                    DialogCaller.showObjDialog(frame,
                            Storage.getInstance().get(table.getSelectedRow()));
                }
            }
        });

        tableScrollPane = new JScrollPane(table);

        tableModel = (AbstractTableModel) table.getModel();
    }

    private void createButtons() {
        btnCar = new CustomJButton(GuiConsts.CAR);
        btnCar.addActionListener(actionEvent -> DialogCaller
                .showObjDialog(frame, DialogCar.class));

        btnTruck = new CustomJButton(GuiConsts.TRUCK);
        btnTruck.addActionListener(actionEvent -> DialogCaller
                .showObjDialog(frame, DialogTruck.class));

        btnMotorcycle = new CustomJButton(GuiConsts.MOTORCYCLE);
        btnMotorcycle.addActionListener(actionEvent -> DialogCaller
                .showObjDialog(frame, DialogMotorcycle.class));

        btnDelete = new CustomJButton(GuiConsts.DELETE);
        btnDelete.addActionListener(actionEvent -> {
            Utils.runInThread(() -> {
                try {
                    Long[] idsMarkedForDelete = Storage.getInstance().getAll().stream()
                            .filter(dataObj -> dataObj.isMarkedForDelete())
                            .map(dataObj -> Long.valueOf(dataObj.getId()))
                            .toArray(Long[]::new);
                    if (idsMarkedForDelete.length > 0) {
                        DaoSQLite.getInstance().delete(idsMarkedForDelete);
                        Storage.getInstance().refresh(DaoSQLite.getInstance().readAll());
                    }
                } catch (SQLException e) {
                    Utils.logAndShowError(LOG, frame,
                            "Error while delete:\n" + e.getLocalizedMessage(),
                            "Deletion error", e);
                }
            });
        });
    }

    private void createPanels() {
        panelTable = new CustomJPanel(PanelType.FRAME_TABLE_PANEL);
        panelTable.add(dbLabel, BorderLayout.NORTH);
        panelTable.add(tableScrollPane, BorderLayout.CENTER);

        panelButton = new CustomJPanel(PanelType.FRAME_BUTTON_PANEL);
        panelButton.add(btnCar);
        panelButton.add(btnTruck);
        panelButton.add(btnMotorcycle);
        panelButton.add(btnDelete);
    }

    private void createMenu() {
        var createOfOpenItem = new JMenuItem(GuiConsts.CREATE_OR_OPEN);
        createOfOpenItem.addActionListener(new CreateOrOpenActionListener(frame, dbLabel));
        var fileMenu = new JMenu(GuiConsts.FILE);
        fileMenu.add(createOfOpenItem);

        var defaultItem = new JCheckBoxMenuItem(GuiConsts.DEFAULT);
        defaultItem.setSelected(Settings.STYLE.equalsIgnoreCase(GuiConsts.DEFAULT));
        var systemItem = new JCheckBoxMenuItem(GuiConsts.SYSTEM);
        systemItem.setSelected(Settings.STYLE.equalsIgnoreCase(GuiConsts.SYSTEM));
        defaultItem.addActionListener(actionEvent -> {
            styleSelectAction(systemItem, defaultItem);
        });
        systemItem.addActionListener(actionEvent -> {
            styleSelectAction(defaultItem, systemItem);
        });
        var styleMenu = new JMenu(GuiConsts.STYLE);
        styleMenu.add(defaultItem);
        styleMenu.add(systemItem);

        var aboutItem = new JMenuItem(GuiConsts.ABOUT);
        aboutItem.addActionListener(actionEvent -> JOptionPane.showMessageDialog(
                frame, GuiConsts.ABOUT_TEXT, GuiConsts.ABOUT_TITLE,
                JOptionPane.INFORMATION_MESSAGE));
        var helpMenu = new JMenu(GuiConsts.HELP);
        helpMenu.add(aboutItem);

        menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(styleMenu);
        menuBar.add(helpMenu);
    }

    private void styleSelectAction(JCheckBoxMenuItem item1, JCheckBoxMenuItem item2) {
        try {
            item1.setSelected(!item2.isSelected());

            if (item1.isSelected()) {
                Settings.writeSetting(Settings.STYLE_SETTING_NAME,
                        item1.getText().toLowerCase(Locale.ROOT));
            } else {
                Settings.writeSetting(Settings.STYLE_SETTING_NAME,
                        item2.getText().toLowerCase(Locale.ROOT));
            }
            setStyle(Settings.STYLE);
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (Exception e) {
            Utils.logAndShowError(LOG, frame,
                    "Error while choosing style.", "Style error", e);
        }
    }

    private void createFrame() {
        frame = new CustomJFrame();
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(panelTable, BorderLayout.CENTER);
        frame.getContentPane().add(panelButton, BorderLayout.EAST);
    }

    private void madeGuiVisible() {
        frame.setVisible(true);
    }

    private static class CreateOrOpenActionListener implements ActionListener {

        private final Component parent;
        private final JLabel dbLabel;

        public CreateOrOpenActionListener(Component parent, JLabel dbLabel) {
            this.parent = parent;
            this.dbLabel = dbLabel;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Utils.runInThread(() -> {
                try {
                    new CustomJFileChooser(parent).showCreateOrOpen();
                    DbInitializer.createTableIfNotExists();
                    Storage.getInstance().refresh(DaoSQLite.getInstance().readAll());
                    dbLabel.setText(Settings.DB_FILE_PATH);
                } catch (IOException e) {
                    Utils.logAndShowError(LOG, parent, "Error while create/open DB file.",
                            "Create/Open file error.", e);
                    System.exit(1);
                } catch (SQLException e) {
                    Utils.logAndShowError(LOG, parent,
                            "Error while read selected DB file.\n" + e.getLocalizedMessage(),
                            "Read selected DB error", e);
                    System.exit(1);
                }
            });
        }
    }
}
