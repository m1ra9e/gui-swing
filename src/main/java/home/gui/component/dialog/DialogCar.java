/*******************************************************************************
 * Copyright 2021-2025 Lenar Shamsutdinov
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
import home.model.Car;
import home.model.VehicleType;

@SuppressWarnings("serial")
public final class DialogCar extends AbstractDialogTrailer {

    private JCheckBox chkPassengers;

    public DialogCar(int width, int height, AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        super(VehicleType.CAR.name(), width, height, dataObj, tblRowOfSelectedDataObj);
    }

    @Override
    protected void createDataComponents() {
        super.createDataComponents();
        chkPassengers = new JCheckBox(GuiConst.TRANSPORTS_PASSENGERS);

        if (!isNewDataObj) {
            chkPassengers.setSelected(((Car) dataObj).isTransportsPassengers());
        }
    }

    @Override
    protected void createPanels() {
        super.createPanels();
        panelTextFields.add(chkPassengers);
    }

    @Override
    protected void createDataObj() {
        dataObj = new Car();
    }

    @Override
    protected void fillDataObj() {
        super.fillDataObj();
        ((Car) dataObj).setTransportsPassengers(chkPassengers.isSelected());
    }
}
