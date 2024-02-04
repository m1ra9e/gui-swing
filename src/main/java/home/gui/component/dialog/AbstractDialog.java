package home.gui.component.dialog;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.function.Predicate;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXDatePicker;

import home.gui.DataActionInGui;
import home.gui.IGuiConsts;
import home.gui.component.CustomJButton;
import home.gui.component.CustomJLabel;
import home.gui.component.CustomJPanel;
import home.gui.component.CustomJPanel.PanelType;
import home.gui.component.CustomJTextField;
import home.gui.component.CustomJXDatePicker;
import home.model.AbstractVehicle;

@SuppressWarnings("serial")
public abstract sealed class AbstractDialog
        extends AbstractCustomJDialog permits AbstractDialogTrailer,DialogMotorcycle {

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

    public AbstractDialog(String title, int width, int height,
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
        lblColor = CustomJLabel.create(IGuiConsts.COLOR);
        lblNumber = CustomJLabel.create(IGuiConsts.NUMBER);
        lblDate = CustomJLabel.create(IGuiConsts.DATE);

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
        btnSave = CustomJButton.create(IGuiConsts.OK);
        btnSave.addActionListener(actionEvent -> {
            fillDataObj();
            if (checkObjFilling()) {
                DataActionInGui.update(dataObj, tblRowOfSelectedDataObj);
                dispose();
            }
        });
        btnCancel = CustomJButton.create(IGuiConsts.CANCEL);
        btnCancel.addActionListener(actionEvent -> dispose());
    }

    protected void createPanels() {
        panelTextFields = CustomJPanel.create(PanelType.DIALOG_TEXT_FIELDS_PANEL);
        panelTextFields.add(lblColor);
        panelTextFields.add(tfColor);
        panelTextFields.add(lblNumber);
        panelTextFields.add(tfNumber);
        panelTextFields.add(lblDate);
        panelTextFields.add(tfDate);

        panelButtons = CustomJPanel.create(PanelType.DIALOG_BUTTON_PANEL);
        panelButtons.add(btnSave);
        panelButtons.add(btnCancel);
    }

    private void createDialog() {
        getContentPane().add(panelTextFields, BorderLayout.CENTER);
        getContentPane().add(panelButtons, BorderLayout.SOUTH);
        makeDialogVisible();
    }

    /**
     * Creating and displaying a form. When launched via
     * "SwingUtilities.invokeLater(new Runnable(){...}" the dialog will be created
     * and displayed after all expected events have been processed, i.e. the dialog
     * will be created and displayed when all resources are ready. This is
     * necessary, so that all elements are guaranteed to be displayed in the window
     * (if you do "setVisible(true)" from the main thread, then there is a chance
     * that some element will not be displayed in the window).
     */
    private void makeDialogVisible() {
        SwingUtilities.invokeLater(() -> setVisible(true));
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
