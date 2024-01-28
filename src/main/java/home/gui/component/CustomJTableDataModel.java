package home.gui.component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

import javax.swing.table.AbstractTableModel;

import home.IConsts;
import home.gui.IGuiConsts;
import home.models.AbstractVehicle;

@SuppressWarnings("serial")
final class CustomJTableDataModel extends AbstractTableModel {

    private static final int COLUMNS_COUNT = 5;

    static final int TYPE_COL_IDX = 0;
    static final int COLOR_COL_IDX = 1;
    static final int NUMBER_COL_IDX = 2;
    static final int DATE_COL_IDX = 3;
    static final int DEL_MARK_COL_IDX = 4;

    private final List<AbstractVehicle> dataObjs;

    CustomJTableDataModel(List<AbstractVehicle> dataObjs) {
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
        return COLUMNS_COUNT;
    }

    // The method returns the title of the column by its index. We have 5 fields,
    // 5 columns. Inside the method, we check the index and return
    // the corresponding column name.
    @Override
    public String getColumnName(int column) {
        switch (column) {
        case TYPE_COL_IDX:
            return IGuiConsts.TYPE;
        case COLOR_COL_IDX:
            return IGuiConsts.COLOR;
        case NUMBER_COL_IDX:
            return IGuiConsts.NUMBER;
        case DATE_COL_IDX:
            return IGuiConsts.DATE;
        case DEL_MARK_COL_IDX:
            return IGuiConsts.DELETION_MARK;
        default:
            return IConsts.EMPTY_STRING;
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
        case TYPE_COL_IDX:
            return dataObj.getType();
        case COLOR_COL_IDX:
            return dataObj.getColor();
        case NUMBER_COL_IDX:
            return dataObj.getNumber();
        case DATE_COL_IDX:
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(dataObj.getDateTime()),
                    TimeZone.getDefault().toZoneId());
            return dateTime.format(IGuiConsts.DATE_FORMATTER);
        case DEL_MARK_COL_IDX:
            return dataObj.isMarkedForDelete();
        default:
            return IConsts.EMPTY_STRING;
        }
    }

    // For adding checkboxes to the second cell.
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == DEL_MARK_COL_IDX ? Boolean.class
                : super.getColumnClass(columnIndex);
    }

    // To make checkboxes editable.
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == DEL_MARK_COL_IDX;
    }

    // To refresh the state of the checkbox in the table after editing it, and
    // save the new state in the data object associated with the checkbox.
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        dataObjs.get(rowIndex).setMarkedForDelete((Boolean) aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}