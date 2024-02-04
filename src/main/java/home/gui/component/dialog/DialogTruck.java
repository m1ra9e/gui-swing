package home.gui.component.dialog;

import javax.swing.JCheckBox;

import home.gui.IGuiConsts;
import home.model.AbstractVehicle;
import home.model.Truck;
import home.model.VehicleType;

@SuppressWarnings("serial")
public final class DialogTruck extends AbstractDialogTrailer {

    private JCheckBox chkCargo;

    public DialogTruck(int width, int height, AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        super(VehicleType.TRUCK.name(), width, height, dataObj, tblRowOfSelectedDataObj);
    }

    @Override
    protected void createDataComponents() {
        super.createDataComponents();
        chkCargo = new JCheckBox(IGuiConsts.TRANSPORTS_CARGO);

        if (!isNewDataObj) {
            chkCargo.setSelected(((Truck) dataObj).isTransportsCargo());
        }
    }

    @Override
    protected void createPanels() {
        super.createPanels();
        panelTextFields.add(chkCargo);
    }

    @Override
    protected void createDataObj() {
        dataObj = new Truck();
    }

    @Override
    protected void fillDataObj() {
        super.fillDataObj();
        ((Truck) dataObj).setTransportsCargo(chkCargo.isSelected());
    }
}
