package home.gui.component.dialog;

import javax.swing.JCheckBox;

import home.gui.IGuiConsts;
import home.model.AbstractVehicle;
import home.model.Car;
import home.model.VehicleType;

@SuppressWarnings("serial")
public final class DialogCar extends AbstractDialogTrailer {

    private JCheckBox chkPassengers;

    public DialogCar(int width, int height, AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        super(VehicleType.CAR.name(), width, height, dataObj, tblRowOfSelectedDataObj);
    }

    @Override
    protected void createDataComponents() {
        super.createDataComponents();
        chkPassengers = new JCheckBox(IGuiConsts.TRANSPORTS_PASSENGERS);

        if (!isNewDataObj) {
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
