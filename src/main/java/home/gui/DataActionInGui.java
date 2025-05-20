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
package home.gui;

import java.util.List;

import home.Storage;
import home.model.AbstractVehicle;

public final class DataActionInGui {

    public static void init(List<AbstractVehicle> dataObjs) {
        Storage.INSTANCE.initDataObjs(dataObjs);
        Gui.INSTANCE.refreshTable();
    }

    public static void add(List<AbstractVehicle> dataObjs) {
        Storage.INSTANCE.addDataObj(dataObjs);
        Gui.INSTANCE.refreshTable();
    }

    public static void update(AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        Storage.INSTANCE.updateDataObj(dataObj, tblRowOfSelectedDataObj);
        Gui.INSTANCE.refreshTable();
    }

    public static void delete(List<AbstractVehicle> objsMarkedForDelete) {
        Storage.INSTANCE.deleteDataObjs(objsMarkedForDelete);
        Gui.INSTANCE.refreshTable();
    }

    private DataActionInGui() {
    }
}
