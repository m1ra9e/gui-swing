package home.gui.component;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import home.Storage;

@SuppressWarnings("serial")
public class CustomJTable extends JTable {

    private static final int FIRST_COL_MIN_WIDTH = 100;
    private static final int SECOND_COL_MIN_WIDTH = 50;
    private static final int THIRD_COL_MIN_WIDTH = 70;
    private static final int FOURTH_COL_MIN_WIDTH = 130;

    private static final int FIFTH_COL_MIN_WIDTH = 50;
    private static final int FIFTH_COL_MAX_WIDTH = 200;
    private static final int FIFTH_COL_PREF_WIDTH = 130;

    public CustomJTable() {
        setModel(new CustomJTableDataModel(Storage.getInstance().getAll()));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        getColumnModel().getColumn(0).setMinWidth(FIRST_COL_MIN_WIDTH);
        getColumnModel().getColumn(1).setMinWidth(SECOND_COL_MIN_WIDTH);
        getColumnModel().getColumn(2).setMinWidth(THIRD_COL_MIN_WIDTH);
        getColumnModel().getColumn(3).setMinWidth(FOURTH_COL_MIN_WIDTH);

        getColumnModel().getColumn(4).setMinWidth(FIFTH_COL_MIN_WIDTH);
        getColumnModel().getColumn(4).setMaxWidth(FIFTH_COL_MAX_WIDTH);
        getColumnModel().getColumn(4).setPreferredWidth(FIFTH_COL_PREF_WIDTH);

        // setPreferredScrollableViewportSize(new Dimension(450, 150));
    }
}
