package home.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;

import home.Storage;
import home.db.dao.DaoSQLite;
import home.gui.component.CustomJButton;
import home.gui.component.CustomJFrame;
import home.gui.component.CustomJPanel;
import home.gui.component.CustomJPanel.PanelType;
import home.gui.component.CustomJTable;
import home.gui.component.dialog.DialogCar;
import home.gui.component.dialog.DialogMotorcycle;
import home.gui.component.dialog.DialogTruck;
import home.utils.Utils;

public class Gui {

    private static final int CLICK_COUNT = 2;

    private static Gui instance;

    private JTable table;
    private AbstractTableModel tableModel;
    private JScrollPane tableScrollPane;

    private CustomJButton btnCar;
    private CustomJButton btnTruck;
    private CustomJButton btnMotorcycle;
    private CustomJButton btnDelete;

    private CustomJPanel panelTable;
    private CustomJPanel panelButton;
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

    public void buildGui() throws Exception {
        // TODO сделать переключение между схемами (результат сохранять в property файл)
        // activateSystemColorScheme();

        JFrame.setDefaultLookAndFeelDecorated(true);

        createTable();
        createButtons();
        createPanels();

        createFrame();

        addButtonListeners();

        madeGuiVisible();
    }

    private void activateSystemColorScheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Error while set the system color scheme.\n"
                            + "Default color scheme will be used.\n"
                            + e.getLocalizedMessage(),
                    "System color scheme error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createTable() {
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
                    JOptionPane.showMessageDialog(frame,
                            "Error while delete:\n" + e.getLocalizedMessage(),
                            "Deletion error",
                            JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            });
        });
    }

    private void createPanels() {
        panelTable = new CustomJPanel(PanelType.FRAME_TABLE_PANEL);

        panelButton = new CustomJPanel(PanelType.FRAME_BUTTON_PANEL);

        panelTable.add(tableScrollPane, BorderLayout.CENTER);

        panelButton.add(btnCar);
        panelButton.add(btnTruck);
        panelButton.add(btnMotorcycle);
        panelButton.add(btnDelete);
    }

    private void createFrame() {
        frame = new CustomJFrame();
        frame.getContentPane().add(panelTable, BorderLayout.CENTER);
        frame.getContentPane().add(panelButton, BorderLayout.EAST);
    }

    private void addButtonListeners() {
    }

    private void madeGuiVisible() {
        frame.setVisible(true);
    }
}
