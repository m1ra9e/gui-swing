package home.gui.component.dialog;

import javax.swing.JCheckBox;

import home.gui.IGuiConsts;
import home.models.AbstractVehicle;
import home.models.AbstractVehicleWithTrailer;

@SuppressWarnings("serial")
abstract class AbstractDialogTrailer extends AbstractDialog {

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
