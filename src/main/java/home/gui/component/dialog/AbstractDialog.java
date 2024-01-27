package home.gui.component.dialog;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.function.Predicate;

import org.apache.log4j.Logger;

import home.Storage;
import home.gui.Gui;
import home.gui.GuiConsts;
import home.gui.component.CustomJButton;
import home.gui.component.CustomJDialog;
import home.gui.component.CustomJLabel;
import home.gui.component.CustomJPanel;
import home.gui.component.CustomJPanel.PanelType;
import home.gui.component.CustomJTextField;
import home.gui.component.CustomJXDatePicker;
import home.models.AbstractVehicle;

@SuppressWarnings("serial")
public abstract class AbstractDialog extends CustomJDialog {

    private static final Logger LOG = Logger.getLogger(AbstractDialog.class);

    private static final int TEXT_FIELD_COLUMN_NUMBERS = 9;

    protected AbstractVehicle dataObj;
    protected boolean isNewDataObj;
    protected int tblRowOfSelectedDataObj;

    private CustomJLabel lblColor;
    private CustomJLabel lblNumber;
    private CustomJLabel lblDate;

    private CustomJTextField tfColor;
    private CustomJTextField tfNumber;
    private CustomJXDatePicker tfDate;

    private CustomJButton btnSave;
    private CustomJButton btnCancel;

    protected CustomJPanel panelTextFields;
    private CustomJPanel panelButtons;

    public AbstractDialog(String title, int width, int height,
            AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        super(title, width, height);
        this.tblRowOfSelectedDataObj = tblRowOfSelectedDataObj;
        if (dataObj != null) {
            this.dataObj = dataObj;
            isNewDataObj = false;
        } else {
            createDataObj();
            isNewDataObj = true;
        }
        createDataComponents();
        createButtons();
        createPanels();
        createDialog();
    }

    protected void createDataComponents() {
        lblColor = new CustomJLabel(GuiConsts.COLOR);
        lblNumber = new CustomJLabel(GuiConsts.NUMBER);
        lblDate = new CustomJLabel(GuiConsts.DATE);

        tfColor = new CustomJTextField(TEXT_FIELD_COLUMN_NUMBERS);
        tfNumber = new CustomJTextField(TEXT_FIELD_COLUMN_NUMBERS);

        tfDate = new CustomJXDatePicker(new Date());

        if (!isNewDataObj) {
            tfColor.setText(dataObj.getColor());
            tfNumber.setText(dataObj.getNumber());
            tfDate.setDate(new Date(dataObj.getDateTime()));
        }
    }

    private void createButtons() {
        btnSave = new CustomJButton(GuiConsts.OK);
        btnSave.addActionListener(actionEvent -> {
            fillDataObj();
            if (checkObjFilling()) {
                Storage.getInstance().updateStorage(dataObj, tblRowOfSelectedDataObj);
                Gui.getInstance().refreshTable();
                dispose();
            }
        });
        btnCancel = new CustomJButton(GuiConsts.CANCEL);
        btnCancel.addActionListener(actionEvent -> dispose());
    }

    protected void createPanels() {
        panelTextFields = new CustomJPanel(PanelType.DIALOG_TEXT_FIELDS_PANEL);

        panelButtons = new CustomJPanel(PanelType.DIALOG_BUTTON_PANEL);

        panelTextFields.add(lblColor);
        panelTextFields.add(tfColor);
        panelTextFields.add(lblNumber);
        panelTextFields.add(tfNumber);
        panelTextFields.add(lblDate);
        panelTextFields.add(tfDate);

        panelButtons.add(btnSave);
        panelButtons.add(btnCancel);
    }

    private void createDialog() {
        getContentPane().add(panelTextFields, BorderLayout.CENTER);
        getContentPane().add(panelButtons, BorderLayout.SOUTH);
    }

    protected void fillDataObj() {
        dataObj.setColor(tfColor.getText());
        dataObj.setNumber(tfNumber.getText());
        dataObj.setDateTime(tfDate.getDate().getTime());
    }

    protected abstract void createDataObj();

    private boolean checkObjFilling() {
        Predicate<String> isFilled = str -> str != null && !str.isBlank();
        return isFilled.test(dataObj.getColor())
                && isFilled.test(dataObj.getNumber());
    }
}
