/*******************************************************************************
 * Copyright 2021-2024 Lenar Shamsutdinov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package home.gui;

import java.lang.reflect.Constructor;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.Storage;
import home.gui.component.dialog.AbstractDialogVehicle;
import home.gui.component.dialog.DialogCar;
import home.gui.component.dialog.DialogMotorcycle;
import home.gui.component.dialog.DialogTruck;
import home.model.AbstractVehicle;
import home.model.VehicleType;
import home.utils.LogUtils;

final class DialogCaller {

    private static final Logger LOG = LoggerFactory.getLogger(DialogCaller.class);

    private static final int OBJ_DIALOG_WIDTH = 450;
    private static final int OBJ_DIALOG_HEIGHT = 350;

    //// in the 'main' part of this project, reflection is used only for
    //// demonstration, in prod-projects I use reflection only in 'test' part
    //// (in the 'main' part of prod-projects, I use reflection only if the logic of
    //// the library or framework I'm using requires it)

    @SuppressWarnings("unchecked")
    static <T extends AbstractDialogVehicle> void showObjDialog(JFrame frame,
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

    static <T extends AbstractDialogVehicle> void showObjDialog(JFrame frame,
            Class<T> dialogClass) {
        showObjDialog(frame, dialogClass, null, Storage.NO_ROW_IS_SELECTED);
    }

    private static <T extends AbstractDialogVehicle> void showObjDialog(JFrame frame,
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
