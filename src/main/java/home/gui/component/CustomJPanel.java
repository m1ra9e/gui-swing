package home.gui.component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public final class CustomJPanel extends JPanel {

    public enum PanelType {
        FRAME_TABLE_PANEL,
        FRAME_BUTTON_PANEL,
        DIALOG_TEXT_FIELDS_PANEL,
        DIALOG_BUTTON_PANEL
    }

    // main table panel sizes
    public static final int FRAME_TBL_PANEL_PREF_WIDTH = 302;
    public static final int FRAME_TBL_PANEL_PREF_HEIGHT = 402;
    public static final int FRAME_TBL_PANEL_MIN_WIDTH = 202;
    public static final int FRAME_TBL_PANEL_MIN_HEIGHT = 102;
    public static final int FRAME_TBL_PANEL_BORDER_LAYOUT_GAP = 2;

    // main button panel sizes
    public static final int FRAME_BTN_PANEL_PREF_WIDTH = 152;
    public static final int FRAME_BTN_PANEL_PREF_HEIGHT = 402;
    public static final int FRAME_BTN_PANEL_MIN_WIDTH = 102;
    public static final int FRAME_BTN_PANEL_MIN_HEIGHT = 102;
    public static final int FRAME_BTN_PANEL_GRID_LAYOUT_ROWS = 8;
    public static final int FRAME_BTN_PANEL_GRID_LAYOUT_COLUMNS = 1;
    public static final int FRAME_BTN_PANEL_GRID_LAYOUT_GAP = 10;

    // dialog text fields panel sizes
    public static final int DIALOG_TXT_FIELDS_PANEL_WIDTH = 450;
    public static final int DIALOG_TXT_FIELDS_PANEL_HEIGHT = 300;
    public static final int DIALOG_TXT_FIELDS_PANEL_GRID_LAYOUT_ROWS = 8;
    public static final int DIALOG_TXT_FIELDS_PANEL_GRID_LAYOUT_COLUMNS = 1;
    public static final int DIALOG_TXT_FIELDS_PANEL_GRID_LAYOUT_GAP = 10;

    // dialog button panel sizes
    public static final int DIALOG_BTN_PANEL_WIDTH = 450;
    public static final int DIALOG_BTN_PANEL_HEIGHT = 50;
    public static final int DIALOG_BTN_PANEL_FLOW_LAYOUT_H_GAP = 10;
    public static final int DIALOG_BTN_PANEL_FLOW_LAYOUT_V_GAP = 2;

    public static final int EMPTY_BORDER_SIZE = 10;

    private CustomJPanel() {
    }

    public static CustomJPanel create(PanelType panelType) {
        var panel = new CustomJPanel();
        panel.setPanelParams(panelType);
        return panel;
    }

    private void setPanelParams(PanelType panelType) {
        String panelName = panelType.name();
        switch (panelType) {
            case FRAME_TABLE_PANEL -> setPanelParams(panelName,
                    FRAME_TBL_PANEL_PREF_WIDTH, FRAME_TBL_PANEL_PREF_HEIGHT,
                    FRAME_TBL_PANEL_MIN_WIDTH, FRAME_TBL_PANEL_MIN_HEIGHT,
                    new BorderLayout(FRAME_TBL_PANEL_BORDER_LAYOUT_GAP,
                            FRAME_TBL_PANEL_BORDER_LAYOUT_GAP));

            case FRAME_BUTTON_PANEL -> setPanelParams(panelName,
                    FRAME_BTN_PANEL_PREF_WIDTH, FRAME_BTN_PANEL_PREF_HEIGHT,
                    FRAME_BTN_PANEL_MIN_WIDTH, FRAME_BTN_PANEL_MIN_HEIGHT,
                    new GridLayout(FRAME_BTN_PANEL_GRID_LAYOUT_ROWS,
                            FRAME_BTN_PANEL_GRID_LAYOUT_COLUMNS,
                            FRAME_BTN_PANEL_GRID_LAYOUT_GAP,
                            FRAME_BTN_PANEL_GRID_LAYOUT_GAP));

            case DIALOG_TEXT_FIELDS_PANEL -> setPanelParams(panelName,
                    DIALOG_TXT_FIELDS_PANEL_WIDTH, DIALOG_TXT_FIELDS_PANEL_HEIGHT,
                    new GridLayout(DIALOG_TXT_FIELDS_PANEL_GRID_LAYOUT_ROWS,
                            DIALOG_TXT_FIELDS_PANEL_GRID_LAYOUT_COLUMNS,
                            DIALOG_TXT_FIELDS_PANEL_GRID_LAYOUT_GAP,
                            DIALOG_TXT_FIELDS_PANEL_GRID_LAYOUT_GAP));

            case DIALOG_BUTTON_PANEL -> setPanelParams(panelName,
                    DIALOG_BTN_PANEL_WIDTH, DIALOG_BTN_PANEL_HEIGHT,
                    new FlowLayout(FlowLayout.CENTER,
                            DIALOG_BTN_PANEL_FLOW_LAYOUT_H_GAP,
                            DIALOG_BTN_PANEL_FLOW_LAYOUT_V_GAP));
        }
    }

    private void setPanelParams(String name, int width, int height,
            int minWidth, int minHeight, LayoutManager layout) {
        setName(name);
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(minWidth, minHeight));
        setLayout(layout);
        setBorder(new EmptyBorder(EMPTY_BORDER_SIZE, EMPTY_BORDER_SIZE,
                EMPTY_BORDER_SIZE, EMPTY_BORDER_SIZE));
    }

    private void setPanelParams(String name, int width, int height, LayoutManager layout) {
        setPanelParams(name, width, height, width, height, layout);
    }
}
