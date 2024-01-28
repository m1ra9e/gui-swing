package home.gui.component;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import home.Storage;

@SuppressWarnings("serial")
public final class CustomJTable extends JTable {

    private static final int TYPE_MIN_WIDTH = 100;
    private static final int COLOR_MIN_WIDTH = 50;
    private static final int NUMBER_MIN_WIDTH = 70;
    private static final int DATE_MIN_WIDTH = 130;

    private static final int DEL_MARK_MIN_WIDTH = 87;
    private static final int DEL_MARK_MAX_WIDTH = 87;
    private static final int DEL_MARK_PREF_WIDTH = 87;

    private CustomJTable() {
    }

    public static CustomJTable create() {
        var table = new CustomJTable();
        table.setModel(new CustomJTableDataModel(Storage.INSTANCE.getAll()));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        table.setColMinWidth(CustomJTableDataModel.TYPE_COL_IDX, TYPE_MIN_WIDTH);
        table.setColMinWidth(CustomJTableDataModel.COLOR_COL_IDX, COLOR_MIN_WIDTH);
        table.setColMinWidth(CustomJTableDataModel.NUMBER_COL_IDX, NUMBER_MIN_WIDTH);
        table.setColMinWidth(CustomJTableDataModel.DATE_COL_IDX, DATE_MIN_WIDTH);

        table.setColWidths(CustomJTableDataModel.DEL_MARK_COL_IDX,
                DEL_MARK_MIN_WIDTH, DEL_MARK_MAX_WIDTH, DEL_MARK_PREF_WIDTH);

        // table.setPreferredScrollableViewportSize(new Dimension(400, 150));
        return table;
    }

    private void setColMinWidth(int colPosition, int width) {
        getColumnModel().getColumn(colPosition).setMinWidth(width);
    }

    private void setColWidths(int colPosition, int minWidth, int maxWidth, int prefWidth) {
        TableColumn tblCol = getColumnModel().getColumn(colPosition);
        tblCol.setMinWidth(minWidth);
        tblCol.setMaxWidth(maxWidth);
        tblCol.setPreferredWidth(prefWidth);
    }
}
