package home.gui.component.dialog;

import javax.swing.JCheckBox;

import home.gui.GuiConsts;
import home.models.AbstractVehicle;
import home.models.AbstractVehicleWithTrailer;

@SuppressWarnings("serial")
public abstract class AbstractDialogTrailer extends AbstractDialog {

    private JCheckBox chkHasTrailer;

    public AbstractDialogTrailer(String title, int width, int height,
            AbstractVehicle dataObj) {
        super(title, width, height, dataObj);
    }

    @Override
    protected void createDataComponents() {
        super.createDataComponents();
        chkHasTrailer = new JCheckBox(GuiConsts.HAS_TRAILER);

        if (dataObj != null) {
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
