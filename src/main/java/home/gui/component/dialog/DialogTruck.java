package home.gui.component.dialog;

import javax.swing.JCheckBox;

import home.gui.GuiConsts;
import home.models.AbstractVehicle;
import home.models.Truck;
import home.models.VehicleType;

@SuppressWarnings("serial")
public class DialogTruck extends AbstractDialogTrailer {

    private JCheckBox chkCargo;

    public DialogTruck(int width, int height, AbstractVehicle dataObj) {
        super(VehicleType.TRUCK.name(), width, height, dataObj);
    }

    @Override
    protected void createDataComponents() {
        super.createDataComponents();
        chkCargo = new JCheckBox(GuiConsts.TRANSPORTS_CARGO);

        if (dataObj != null) {
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
