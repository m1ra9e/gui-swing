package home.gui.component.dialog;

import javax.swing.JCheckBox;

import home.gui.IGuiConsts;
import home.model.AbstractVehicle;
import home.model.Motorcycle;
import home.model.VehicleType;

@SuppressWarnings("serial")
public final class DialogMotorcycle extends AbstractDialog {

    private JCheckBox chkCradle;

    public DialogMotorcycle(int width, int height, AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        super(VehicleType.MOTORCYCLE.name(), width, height, dataObj, tblRowOfSelectedDataObj);
    }

    @Override
    protected void createDataComponents() {
        super.createDataComponents();
        chkCradle = new JCheckBox(IGuiConsts.HAS_CRADLE);

        if (!isNewDataObj) {
            chkCradle.setSelected(((Motorcycle) dataObj).hasCradle());
        }
    }

    @Override
    protected void createPanels() {
        super.createPanels();
        panelTextFields.add(chkCradle);
    }

    @Override
    protected void createDataObj() {
        dataObj = new Motorcycle();
    }

    @Override
    protected void fillDataObj() {
        super.fillDataObj();
        ((Motorcycle) dataObj).setHasCradle(chkCradle.isSelected());
    }
}
