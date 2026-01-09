/*******************************************************************************
 * Copyright 2021-2026 Lenar Shamsutdinov
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
import home.model.Truck;
import home.model.VehicleType;

@SuppressWarnings("serial")
public final class DialogTruck extends AbstractDialogTrailer {

    private JCheckBox chkCargo;

    public DialogTruck(int width, int height, AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        super(VehicleType.TRUCK.name(), width, height, dataObj, tblRowOfSelectedDataObj);
    }

    @Override
    protected void createDataComponents() {
        super.createDataComponents();
        chkCargo = new JCheckBox(GuiConst.TRANSPORTS_CARGO);

        if (!isNewDataObj) {
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
