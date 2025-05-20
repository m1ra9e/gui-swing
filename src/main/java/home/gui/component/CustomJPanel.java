/*******************************************************************************
 * Copyright 2021-2025 Lenar Shamsutdinov
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import home.gui.component.CustomJPanel.PanelType;

public final class CustomJPanel {

    public enum PanelType {
        // main window panels
        MAIN_FRAME_TABLE_PANEL,
        MAIN_FRAME_BUTTON_PANEL,

        // vehicle dialog panels
        VEHICLE_DIALOG_TEXT_FIELDS_PANEL,
        VEHICLE_DIALOG_BUTTON_PANEL,

        // connection dialog panels
        CONNECTION_DIALOG_TEXT_FIELDS_PANEL,
        CONNECTION_DIALOG_BUTTON_PANEL;
    }

    public static AbstractCustomJPanel create(PanelType panelType) {
        AbstractCustomJPanel panel = switch (panelType) {
            case MAIN_FRAME_TABLE_PANEL -> new MainFrameTableJPanel();
            case MAIN_FRAME_BUTTON_PANEL -> new MainFrameButtonJPanel();
            case VEHICLE_DIALOG_TEXT_FIELDS_PANEL -> new VehicleDialogTextFieldsJPanel();
            case VEHICLE_DIALOG_BUTTON_PANEL -> new VehicleDialogButtonJPanel();
            case CONNECTION_DIALOG_TEXT_FIELDS_PANEL -> new ConnectionDialogTextFieldsJPanel();
            case CONNECTION_DIALOG_BUTTON_PANEL -> new ConnectionDialogButtonJPanel();
        };
        panel.fillPanelParams();
        return panel;
    }

    private CustomJPanel() {
    }
}

@SuppressWarnings("serial")
abstract sealed class AbstractCustomJPanel extends
        JPanel permits MainFrameTableJPanel, MainFrameButtonJPanel,
        VehicleDialogTextFieldsJPanel, VehicleDialogButtonJPanel,
        ConnectionDialogTextFieldsJPanel, ConnectionDialogButtonJPanel {

    private static final int EMPTY_BORDER_SIZE = 10;

    protected abstract void fillPanelParams();

    protected abstract PanelType getPanelType();

    protected void setPanelParams(int width, int height,
            int minWidth, int minHeight, LayoutManager layout) {
        setName(getPanelType().name());
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(minWidth, minHeight));
        setLayout(layout);
        setBorder(new EmptyBorder(EMPTY_BORDER_SIZE, EMPTY_BORDER_SIZE,
                EMPTY_BORDER_SIZE, EMPTY_BORDER_SIZE));
    }

    protected void setPanelParams(int width, int height, LayoutManager layout) {
        setPanelParams(width, height, width, height, layout);
    }
}

@SuppressWarnings("serial")
final class MainFrameTableJPanel extends AbstractCustomJPanel {

    // main table panel sizes
    private static final int FRAME_TBL_PANEL_PREF_WIDTH = 300;
    private static final int FRAME_TBL_PANEL_PREF_HEIGHT = 400;
    private static final int FRAME_TBL_PANEL_MIN_WIDTH = 200;
    private static final int FRAME_TBL_PANEL_MIN_HEIGHT = 100;
    private static final int FRAME_TBL_PANEL_BORDER_LAYOUT_GAP = 2;

    @Override
    protected PanelType getPanelType() {
        return PanelType.MAIN_FRAME_TABLE_PANEL;
    }

    @Override
    protected void fillPanelParams() {
        setPanelParams(FRAME_TBL_PANEL_PREF_WIDTH, FRAME_TBL_PANEL_PREF_HEIGHT,
                FRAME_TBL_PANEL_MIN_WIDTH, FRAME_TBL_PANEL_MIN_HEIGHT,
                new BorderLayout(FRAME_TBL_PANEL_BORDER_LAYOUT_GAP,
                        FRAME_TBL_PANEL_BORDER_LAYOUT_GAP));
    }
}

@SuppressWarnings("serial")
final class MainFrameButtonJPanel extends AbstractCustomJPanel {

    // main button panel sizes
    private static final int FRAME_BTN_PANEL_PREF_WIDTH = 150;
    private static final int FRAME_BTN_PANEL_PREF_HEIGHT = 400;
    private static final int FRAME_BTN_PANEL_MIN_WIDTH = 100;
    private static final int FRAME_BTN_PANEL_MIN_HEIGHT = 100;
    private static final int FRAME_BTN_PANEL_GRID_LAYOUT_ROWS = 8;
    private static final int FRAME_BTN_PANEL_GRID_LAYOUT_COLUMNS = 1;
    private static final int FRAME_BTN_PANEL_GRID_LAYOUT_GAP = 10;

    @Override
    protected PanelType getPanelType() {
        return PanelType.MAIN_FRAME_BUTTON_PANEL;
    }

    @Override
    protected void fillPanelParams() {
        setPanelParams(FRAME_BTN_PANEL_PREF_WIDTH, FRAME_BTN_PANEL_PREF_HEIGHT,
                FRAME_BTN_PANEL_MIN_WIDTH, FRAME_BTN_PANEL_MIN_HEIGHT,
                new GridLayout(FRAME_BTN_PANEL_GRID_LAYOUT_ROWS,
                        FRAME_BTN_PANEL_GRID_LAYOUT_COLUMNS,
                        FRAME_BTN_PANEL_GRID_LAYOUT_GAP,
                        FRAME_BTN_PANEL_GRID_LAYOUT_GAP));
    }
}

@SuppressWarnings("serial")
final class VehicleDialogTextFieldsJPanel extends AbstractCustomJPanel {

    // vehicle dialog text fields panel sizes
    private static final int DIALOG_TXT_FIELDS_PANEL_WIDTH = 450;
    private static final int DIALOG_TXT_FIELDS_PANEL_HEIGHT = 300;
    private static final int DIALOG_TXT_FIELDS_PANEL_GRID_LAYOUT_ROWS = 8;
    private static final int DIALOG_TXT_FIELDS_PANEL_GRID_LAYOUT_COLUMNS = 1;
    private static final int DIALOG_TXT_FIELDS_PANEL_GRID_LAYOUT_GAP = 10;

    @Override
    protected PanelType getPanelType() {
        return PanelType.VEHICLE_DIALOG_TEXT_FIELDS_PANEL;
    }

    @Override
    protected void fillPanelParams() {
        setPanelParams(DIALOG_TXT_FIELDS_PANEL_WIDTH, DIALOG_TXT_FIELDS_PANEL_HEIGHT,
                new GridLayout(DIALOG_TXT_FIELDS_PANEL_GRID_LAYOUT_ROWS,
                        DIALOG_TXT_FIELDS_PANEL_GRID_LAYOUT_COLUMNS,
                        DIALOG_TXT_FIELDS_PANEL_GRID_LAYOUT_GAP,
                        DIALOG_TXT_FIELDS_PANEL_GRID_LAYOUT_GAP));
    }
}

@SuppressWarnings("serial")
final class VehicleDialogButtonJPanel extends AbstractCustomJPanel {

    // vehicle dialog button panel sizes
    public static final int DIALOG_BTN_PANEL_WIDTH = 450;
    public static final int DIALOG_BTN_PANEL_HEIGHT = 50;
    public static final int DIALOG_BTN_PANEL_FLOW_LAYOUT_H_GAP = 10;
    public static final int DIALOG_BTN_PANEL_FLOW_LAYOUT_V_GAP = 2;

    @Override
    protected PanelType getPanelType() {
        return PanelType.VEHICLE_DIALOG_BUTTON_PANEL;
    }

    @Override
    protected void fillPanelParams() {
        setPanelParams(DIALOG_BTN_PANEL_WIDTH, DIALOG_BTN_PANEL_HEIGHT,
                new FlowLayout(FlowLayout.CENTER,
                        DIALOG_BTN_PANEL_FLOW_LAYOUT_H_GAP,
                        DIALOG_BTN_PANEL_FLOW_LAYOUT_V_GAP));
    }
}

@SuppressWarnings("serial")
final class ConnectionDialogTextFieldsJPanel extends AbstractCustomJPanel {

    // connection dialog text fields panel sizes
    private static final int CONNECTION_TXT_FIELDS_PANEL_WIDTH = 450;
    private static final int CONNECTION_TXT_FIELDS_PANEL_HEIGHT = 600;
    private static final int CONNECTION_TXT_FIELDS_PANEL_GRID_LAYOUT_ROWS = 13;
    private static final int CONNECTION_TXT_FIELDS_PANEL_GRID_LAYOUT_COLUMNS = 2;
    private static final int CONNECTION_TXT_FIELDS_PANEL_GRID_LAYOUT_GAP = 10;

    @Override
    protected PanelType getPanelType() {
        return PanelType.CONNECTION_DIALOG_TEXT_FIELDS_PANEL;
    }

    @Override
    protected void fillPanelParams() {
        setPanelParams(CONNECTION_TXT_FIELDS_PANEL_WIDTH, CONNECTION_TXT_FIELDS_PANEL_HEIGHT,
                new GridLayout(CONNECTION_TXT_FIELDS_PANEL_GRID_LAYOUT_ROWS,
                        CONNECTION_TXT_FIELDS_PANEL_GRID_LAYOUT_COLUMNS,
                        CONNECTION_TXT_FIELDS_PANEL_GRID_LAYOUT_GAP,
                        CONNECTION_TXT_FIELDS_PANEL_GRID_LAYOUT_GAP));
    }
}

@SuppressWarnings("serial")
final class ConnectionDialogButtonJPanel extends AbstractCustomJPanel {

    // connection dialog button panel sizes
    private static final int CONNECTION_BTN_PANEL_WIDTH = 450;
    private static final int CONNECTION_BTN_PANEL_HEIGHT = 50;
    private static final int CONNECTION_BTN_PANEL_FLOW_LAYOUT_H_GAP = 10;
    private static final int CONNECTION_BTN_PANEL_FLOW_LAYOUT_V_GAP = 2;

    @Override
    protected PanelType getPanelType() {
        return PanelType.CONNECTION_DIALOG_BUTTON_PANEL;
    }

    @Override
    protected void fillPanelParams() {
        setPanelParams(CONNECTION_BTN_PANEL_WIDTH, CONNECTION_BTN_PANEL_HEIGHT,
                new FlowLayout(FlowLayout.CENTER,
                        CONNECTION_BTN_PANEL_FLOW_LAYOUT_H_GAP,
                        CONNECTION_BTN_PANEL_FLOW_LAYOUT_V_GAP));
    }
}