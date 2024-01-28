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
import home.models.AbstractVehicle;
import home.models.VehicleType;
import home.utils.Utils;

public final class DialogCaller {

    private static final Logger LOG = LoggerFactory.getLogger(DialogCaller.class);

    private static final int OBJ_DIALOG_WIDTH = 450;
    private static final int OBJ_DIALOG_HEIGHT = 350;

    @SuppressWarnings("unchecked")
    static <T extends AbstractDialog> void showObjDialog(JFrame frame,
            AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        Class<T> dialogClass = null;
        VehicleType objType = dataObj.getType();
        switch (objType) {
        case CAR:
            dialogClass = (Class<T>) DialogCar.class;
            break;

        case TRUCK:
            dialogClass = (Class<T>) DialogTruck.class;
            break;

        case MOTORCYCLE:
            dialogClass = (Class<T>) DialogMotorcycle.class;
            break;

        default:
            Utils.logAndShowError(LOG, frame,
                    "There is no dialog for [" + objType + "] object type",
                    "Object type error", new IllegalArgumentException("DataObj type error"));
            return;
        }
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
            Utils.logAndShowError(LOG, frame,
                    "Dialog window creation error.\n" + e.getLocalizedMessage(),
                    "Dialog error", e);
            return;
        }
    }

    private DialogCaller() {
    }
}
