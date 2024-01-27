package home.gui.component.dialog;

import javax.swing.JCheckBox;

import home.gui.GuiConsts;
import home.models.AbstractVehicle;
import home.models.Car;
import home.models.VehicleType;

@SuppressWarnings("serial")
public class DialogCar extends AbstractDialogTrailer {

    private JCheckBox chkPassengers;

    public DialogCar(int width, int height, AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        super(VehicleType.CAR.name(), width, height, dataObj, tblRowOfSelectedDataObj);
    }

    @Override
    protected void createDataComponents() {
        super.createDataComponents();
        chkPassengers = new JCheckBox(GuiConsts.TRANSPORTS_PASSENGERS);

        if (dataObj != null) {
            chkPassengers.setSelected(((Car) dataObj).isTransportsPassengers());
        }
    }

    @Override
    protected void createPanels() {
        super.createPanels();
        panelTextFields.add(chkPassengers);
    }

    @Override
    protected void createDataObj() {
        dataObj = new Car();
    }

    @Override
    protected void fillDataObj() {
        super.fillDataObj();
        ((Car) dataObj).setTransportsPassengers(chkPassengers.isSelected());
    }
}
