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
package home.gui.component.dialog;

import javax.swing.JCheckBox;

import home.gui.GuiConst;
import home.model.AbstractVehicle;
import home.model.AbstractVehicleWithTrailer;

@SuppressWarnings("serial")
abstract sealed class AbstractDialogTrailer
        extends AbstractDialogVehicle permits DialogCar, DialogTruck {

    private JCheckBox chkHasTrailer;

    protected AbstractDialogTrailer(String title, int width, int height,
            AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        super(title, width, height, dataObj, tblRowOfSelectedDataObj);
    }

    @Override
    protected void createDataComponents() {
        super.createDataComponents();
        chkHasTrailer = new JCheckBox(GuiConst.HAS_TRAILER);

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
