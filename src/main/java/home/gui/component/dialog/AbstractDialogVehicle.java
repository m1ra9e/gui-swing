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
package home.gui.component.dialog;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.function.Predicate;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXDatePicker;

import home.gui.DataActionInGui;
import home.gui.GuiConst;
import home.gui.component.CustomJButton;
import home.gui.component.CustomJLabel;
import home.gui.component.CustomJPanel;
import home.gui.component.CustomJPanel.PanelType;
import home.gui.component.CustomJTextField;
import home.gui.component.CustomJXDatePicker;
import home.model.AbstractVehicle;

@SuppressWarnings("serial")
public abstract sealed class AbstractDialogVehicle
extends AbstractCustomJDialog permits AbstractDialogTrailer, DialogMotorcycle {

    private static final int TEXT_FIELD_COLUMN_NUMBERS = 9;

    protected AbstractVehicle dataObj;
    protected boolean isNewDataObj;
    protected int tblRowOfSelectedDataObj;

    private JLabel lblColor;
    private JLabel lblNumber;
    private JLabel lblDate;

    private JTextField tfColor;
    private JTextField tfNumber;
    private JXDatePicker tfDate;

    private JButton btnSave;
    private JButton btnCancel;

    protected JPanel panelTextFields;
    private JPanel panelButtons;

    public AbstractDialogVehicle(String title, int width, int height,
            AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        super(title, width, height);
        this.tblRowOfSelectedDataObj = tblRowOfSelectedDataObj;
        if (dataObj == null) {
            isNewDataObj = true;
        } else {
            this.dataObj = dataObj;
            isNewDataObj = false;
        }
    }

    public void buildDialog() {
        init();

        if (isNewDataObj) {
            createDataObj();
        }

        createDataComponents();
        createButtons();
        createPanels();
        createDialog();
    }

    protected void createDataComponents() {
        lblColor = CustomJLabel.create(GuiConst.COLOR);
        lblNumber = CustomJLabel.create(GuiConst.NUMBER);
        lblDate = CustomJLabel.create(GuiConst.DATE);

        tfColor = CustomJTextField.create(TEXT_FIELD_COLUMN_NUMBERS);
        tfNumber = CustomJTextField.create(TEXT_FIELD_COLUMN_NUMBERS);

        tfDate = CustomJXDatePicker.creat(new Date());

        if (!isNewDataObj) {
            tfColor.setText(dataObj.getColor());
            tfNumber.setText(dataObj.getNumber());
            tfDate.setDate(new Date(dataObj.getDateTime()));
        }
    }

    private void createButtons() {
        btnSave = CustomJButton.create(GuiConst.OK);
        btnSave.addActionListener(actionEvent -> {
            fillDataObj();
            if (checkObjFilling()) {
                DataActionInGui.update(dataObj, tblRowOfSelectedDataObj);
                dispose();
            }
        });
        btnCancel = CustomJButton.create(GuiConst.CANCEL);
        btnCancel.addActionListener(actionEvent -> dispose());
    }

    protected void createPanels() {
        panelTextFields = CustomJPanel.create(PanelType.VEHICLE_DIALOG_TEXT_FIELDS_PANEL);
        panelTextFields.add(lblColor);
        panelTextFields.add(tfColor);
        panelTextFields.add(lblNumber);
        panelTextFields.add(tfNumber);
        panelTextFields.add(lblDate);
        panelTextFields.add(tfDate);

        panelButtons = CustomJPanel.create(PanelType.VEHICLE_DIALOG_BUTTON_PANEL);
        panelButtons.add(btnSave);
        panelButtons.add(btnCancel);
    }

    private void createDialog() {
        getContentPane().add(panelTextFields, BorderLayout.CENTER);
        getContentPane().add(panelButtons, BorderLayout.SOUTH);
        makeDialogVisible();
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
