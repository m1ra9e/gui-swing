package home.gui;

import java.lang.reflect.Constructor;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import home.gui.component.dialog.AbstractDialog;
import home.gui.component.dialog.DialogCar;
import home.gui.component.dialog.DialogMotorcycle;
import home.gui.component.dialog.DialogTruck;
import home.models.AbstractVehicle;
import home.models.VehicleType;

public class DialogCaller {

    private static final Logger LOG = Logger.getLogger(DialogCaller.class);

    private static final int OBJ_DIALOG_WIDTH = 450;
    private static final int OBJ_DIALOG_HEIGHT = 350;

    public static <T extends AbstractDialog> void showObjDialog(JFrame frame,
            Class<T> dialogClass, AbstractVehicle dataObj) {
        Constructor<T> constructor;
        try {
            constructor = dialogClass.getConstructor(
                    new Class[] { int.class, int.class, AbstractVehicle.class });
            T dialog = constructor.newInstance(OBJ_DIALOG_WIDTH, OBJ_DIALOG_HEIGHT, dataObj);
            dialog.setVisible(true);
        } catch (Exception e) {
            LOG.error("Exception: ", e);
            JOptionPane.showMessageDialog(frame,
                    "Dialog window creation error.\n" + e.getLocalizedMessage(),
                    "Dialog error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    public static <T extends AbstractDialog> void showObjDialog(JFrame frame,
            Class<T> dialogClass) {
        showObjDialog(frame, dialogClass, null);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractDialog> void showObjDialog(JFrame frame,
            AbstractVehicle dataObj) {
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
            JOptionPane.showMessageDialog(frame,
                    "There is no dialog for [" + objType + "] object type",
                    "Object type error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        showObjDialog(frame, dialogClass, dataObj);
    }

    private DialogCaller() {
    }
}
