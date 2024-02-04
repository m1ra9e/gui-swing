package home.gui.component.dialog;

import javax.swing.JCheckBox;

import home.gui.IGuiConsts;
import home.model.AbstractVehicle;
import home.model.AbstractVehicleWithTrailer;

@SuppressWarnings("serial")
abstract sealed class AbstractDialogTrailer
        extends AbstractDialog permits DialogCar,DialogTruck {

    private JCheckBox chkHasTrailer;

    protected AbstractDialogTrailer(String title, int width, int height,
            AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        super(title, width, height, dataObj, tblRowOfSelectedDataObj);
    }

    @Override
    protected void createDataComponents() {
        super.createDataComponents();
        chkHasTrailer = new JCheckBox(IGuiConsts.HAS_TRAILER);

        if (!isNewDataObj) {
            chkHasTrailer.setSelected(((AbstractVehicleWithTrailer) dataObj).hasTrailer());
        }
    }

    @Override
    protected void createPanels() {
        super.createPanels();
        panelTextFields.add(chkHasTrailer);
    }

    @Override
    protected void fillDataObj() {
        super.fillDataObj();
        ((AbstractVehicleWithTrailer) dataObj)
                .setHasTrailer(chkHasTrailer.isSelected());
    }
}
