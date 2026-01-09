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
package home.gui.component;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import home.Const;
import home.gui.GuiConst;
import home.model.AbstractVehicle;
import home.utils.Utils;

@SuppressWarnings("serial")
public final class CustomJTableDataModel extends AbstractTableModel {

    private static final int COLUMNS_COUNT = 5;

    static final int TYPE_COL_IDX = 0;
    static final int COLOR_COL_IDX = 1;
    static final int NUMBER_COL_IDX = 2;
    static final int DATE_COL_IDX = 3;
    static final int DEL_MARK_COL_IDX = 4;

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
        return COLUMNS_COUNT;
    }

    // The method returns the title of the column by its index. We have 5 fields,
    // 5 columns. Inside the method, we check the index and return
    // the corresponding column name.
    @Override
    public String getColumnName(int columnIndex) {
        String columnName = switch (columnIndex) {
            case TYPE_COL_IDX -> GuiConst.TYPE;
            case COLOR_COL_IDX -> GuiConst.COLOR;
            case NUMBER_COL_IDX -> GuiConst.NUMBER;
            case DATE_COL_IDX -> GuiConst.DATE;
            case DEL_MARK_COL_IDX -> GuiConst.DELETION_MARK;
            default -> Const.EMPTY_STRING;
        };
        return columnName;
    }

    // The method is responsible for what data in which cells of the JTable will
    // be shown. The method passes the row and column index of the JTable cell as
    // parameters. Algorithm of work: by the row index we get the corresponding
    // entity from the dataObjs list, and by the column index we find out the data
    // from which field of the AbstractVehicle object needs to be shown.
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AbstractVehicle dataObj = dataObjs.get(rowIndex);

        Object cellValue = switch (columnIndex) {
            case TYPE_COL_IDX -> dataObj.getType();
            case COLOR_COL_IDX -> dataObj.getColor();
            case NUMBER_COL_IDX -> dataObj.getNumber();
            case DATE_COL_IDX -> Utils.getFormattedDate(dataObj.getDateTime());
            case DEL_MARK_COL_IDX -> dataObj.isMarkedForDelete();
            default -> Const.EMPTY_STRING;
        };

        return cellValue;
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