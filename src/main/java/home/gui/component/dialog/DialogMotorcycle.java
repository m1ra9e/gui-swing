package home.gui.component.dialog;

import javax.swing.JCheckBox;

import home.gui.GuiConsts;
import home.models.AbstractVehicle;
import home.models.Motorcycle;
import home.models.VehicleType;

@SuppressWarnings("serial")
public class DialogMotorcycle extends AbstractDialog {

    private JCheckBox chkCradle;

    public DialogMotorcycle(int width, int height, AbstractVehicle dataObj) {
        super(VehicleType.MOTORCYCLE.name(), width, height, dataObj);
    }

    @Override
    protected void createDataComponents() {
        super.createDataComponents();
        chkCradle = new JCheckBox(GuiConsts.HAS_CRADLE);

        if (dataObj != null) {
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
