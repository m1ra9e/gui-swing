package home.gui.component;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import home.gui.GuiConsts;
import home.models.AbstractVehicle;

@SuppressWarnings("serial")
public class CustomJTableDataModel extends AbstractTableModel {

    private static final int TABLE_COLUMN_COUNT = 5;

    private static final int TYPE_COLUMN_INDEX = 0;
    private static final int COLOR_COLUMN_INDEX = 1;
    private static final int NUMBER_COLUMN_INDEX = 2;
    private static final int DATE_COLUMN_INDEX = 3;
    private static final int DELETION_MARK_COLUMN_INDEX = 4;

    private final List<AbstractVehicle> dataObjs;

    public CustomJTableDataModel(List<AbstractVehicle> dataObjs) {
        this.dataObjs = dataObjs;
    }

    // The method returns the number of rows that will be displayed in the table.
    // Here dataObjs is a list. For JTable knowing, the number of rows to be shown,
    // it is enough to get the size from dataObjs.
    @Override
    public int getRowCount() {
        return dataObjs.size();
    }

    // The method returns the number of columns that will be displayed in the table.
    @Override
    public int getColumnCount() {
        return TABLE_COLUMN_COUNT;
    }

    // The method returns the title of the column by its index. We have 5 fields,
    // 5 columns. Inside the method, we check the index and return
    // the corresponding column name.
    @Override
    public String getColumnName(int column) {
        switch (column) {
        case TYPE_COLUMN_INDEX:
            return GuiConsts.TYPE;
        case COLOR_COLUMN_INDEX:
            return GuiConsts.COLOR;
        case NUMBER_COLUMN_INDEX:
            return GuiConsts.NUMBER;
        case DATE_COLUMN_INDEX:
            return GuiConsts.DATE;
        case DELETION_MARK_COLUMN_INDEX:
            return GuiConsts.DELETION_MARK;
        default:
            return "";
        }
    }

    // The method is responsible for what data in which cells of the JTable will
    // be shown. The method passes the row and column index of the JTable cell as
    // parameters. Algorithm of work: by the row index we get the corresponding
    // entity from the dataObjs list, and by the column index we find out the data
    // from which field of the AbstractVehicle object needs to be shown.
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AbstractVehicle dataObj = dataObjs.get(rowIndex);
        switch (columnIndex) {
        case TYPE_COLUMN_INDEX:
            return dataObj.getType();
        case COLOR_COLUMN_INDEX:
            return dataObj.getColor();
        case NUMBER_COLUMN_INDEX:
            return dataObj.getNumber();
        case DATE_COLUMN_INDEX:
            return GuiConsts.DATE_FORMAT.format(new Date(dataObj.getDateTime()));
        case DELETION_MARK_COLUMN_INDEX:
            return dataObj.isMarkedForDelete();
        default:
            return "";
        }
    }

    // For adding checkboxes to the second cell.
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == DELETION_MARK_COLUMN_INDEX ? Boolean.class
                : super.getColumnClass(columnIndex);
    }

    // To make checkboxes editable.
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == DELETION_MARK_COLUMN_INDEX;
    }

    // To refresh the state of the checkbox in the table after editing it, and
    // save the new state in the data object associated with the checkbox.
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        dataObjs.get(rowIndex).setMarkedForDelete((Boolean) aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}