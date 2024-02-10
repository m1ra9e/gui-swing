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
package home.gui.component;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

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

    public static CustomJTable create(AbstractTableModel tableDataModel,
            boolean isAutoResizeTableWidth) {
        var table = new CustomJTable();
        table.setModel(tableDataModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setAutoResize(isAutoResizeTableWidth);

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

    public void setAutoResize(boolean isAutoResizeTableWidth) {
        if (isAutoResizeTableWidth) {
            setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        } else {
            setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }
    }
}
