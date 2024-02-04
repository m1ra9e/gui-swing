package home.gui;

import java.lang.reflect.Constructor;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.Storage;
import home.gui.component.dialog.AbstractDialog;
import home.gui.component.dialog.DialogCar;
import home.gui.component.dialog.DialogMotorcycle;
import home.gui.component.dialog.DialogTruck;
import home.model.AbstractVehicle;
import home.model.VehicleType;
import home.utils.LogUtils;

public final class DialogCaller {

    private static final Logger LOG = LoggerFactory.getLogger(DialogCaller.class);

    private static final int OBJ_DIALOG_WIDTH = 450;
    private static final int OBJ_DIALOG_HEIGHT = 350;

    @SuppressWarnings("unchecked")
    static <T extends AbstractDialog> void showObjDialog(JFrame frame,
            AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        Class<T> dialogClass = null;
        VehicleType objType = dataObj.getType();

        dialogClass = switch (objType) {
            case CAR -> (Class<T>) DialogCar.class;
            case TRUCK -> (Class<T>) DialogTruck.class;
            case MOTORCYCLE -> (Class<T>) DialogMotorcycle.class;
        };

        showObjDialog(frame, dialogClass, dataObj, tblRowOfSelectedDataObj);
    }

    static <T extends AbstractDialog> void showObjDialog(JFrame frame,
            Class<T> dialogClass) {
        showObjDialog(frame, dialogClass, null, Storage.NO_ROW_IS_SELECTED);
    }

    private static <T extends AbstractDialog> void showObjDialog(JFrame frame,
            Class<T> dialogClass, AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        Constructor<T> constructor;
        try {
            constructor = dialogClass.getConstructor(
                    new Class[] { int.class, int.class, AbstractVehicle.class, int.class });
            T blankDialog = constructor.newInstance(OBJ_DIALOG_WIDTH, OBJ_DIALOG_HEIGHT,
                    dataObj, tblRowOfSelectedDataObj);
            blankDialog.buildDialog();
        } catch (Exception e) {
            LogUtils.logAndShowError(LOG, frame,
                    "Dialog window creation error.\n" + e.getLocalizedMessage(),
                    "Dialog error", e);
            return;
        }
    }

    private DialogCaller() {
    }
}
