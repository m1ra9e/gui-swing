package home.gui.component.dialog;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.Date;
import java.util.function.Predicate;

import org.apache.log4j.Logger;

import home.Storage;
import home.db.dao.DaoSQLite;
import home.gui.GuiConsts;
import home.gui.component.CustomJButton;
import home.gui.component.CustomJDialog;
import home.gui.component.CustomJLabel;
import home.gui.component.CustomJPanel;
import home.gui.component.CustomJPanel.PanelType;
import home.gui.component.CustomJTextField;
import home.gui.component.CustomJXDatePicker;
import home.models.AbstractVehicle;
import home.utils.Utils;

@SuppressWarnings("serial")
public abstract class AbstractDialog extends CustomJDialog {

    private static final Logger LOG = Logger.getLogger(AbstractDialog.class);

    private static final int TEXT_FIELD_COLUMN_NUMBERS = 9;

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

    protected AbstractVehicle dataObj;
    protected boolean isNewDataObj;

    public AbstractDialog(String title, int width, int height, AbstractVehicle dataObj) {
        super(title, width, height);
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
        btnSave = new CustomJButton(GuiConsts.SAVE);
        btnSave.addActionListener(actionEvent -> {
            fillDataObj();
            if (checkObjFilling()) {
                saveToDb();
                refreshGuiTbl();
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

    private void saveToDb() {
        try {
            long id = dataObj.getId();
            if (id != 0) {
                dataObj.setId(id);
                DaoSQLite.getInstance().update(dataObj);
            } else {
                DaoSQLite.getInstance().create(dataObj);
            }
        } catch (SQLException e) {
            Utils.logAndShowError(LOG, this, "Error while save to db.", "Save error", e);
        }
    }

    private void refreshGuiTbl() {
        Utils.runInThread(() -> {
            try {
                Storage.getInstance().refresh(DaoSQLite.getInstance().readAll());
            } catch (SQLException e) {
                Utils.logAndShowError(LOG, this, "Error while refresh GUI table.",
                        "Refresh error", e);
            }
        });
    }
}
