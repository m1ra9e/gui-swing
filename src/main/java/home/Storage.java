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
package home;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import home.model.AbstractVehicle;

public enum Storage {

    INSTANCE;

    public static final int NO_ROW_IS_SELECTED = -1;

    private final List<AbstractVehicle> dataObjStorage = new LinkedList<>();
    private final Set<Long> dataObjIdsForDelete = new HashSet<>();
    private final Set<Long> dataObjIdsForUpdate = new HashSet<>();

    public void initDataObjs(List<AbstractVehicle> dataObjs) {
        dataObjIdsForDelete.clear();
        dataObjIdsForUpdate.clear();
        dataObjStorage.clear();
        dataObjStorage.addAll(dataObjs);
    }

    public void addDataObj(List<AbstractVehicle> dataObjs) {
        dataObjStorage.addAll(dataObjs);
    }

    public List<AbstractVehicle> getAll() {
        return dataObjStorage;
    }

    public AbstractVehicle get(int row) {
        return dataObjStorage.get(row);
    }

    public Long[] getIdsForDelete() {
        return dataObjIdsForDelete.stream().map(id -> Long.valueOf(id))
                .toArray(Long[]::new);
    }

    public Set<Long> getIdsForUpdate() {
        return dataObjIdsForUpdate;
    }

    public void updateDataObj(AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        if (NO_ROW_IS_SELECTED == tblRowOfSelectedDataObj) {
            dataObjStorage.add(dataObj);
        } else {
            dataObjStorage.set(tblRowOfSelectedDataObj, dataObj);
            dataObjIdsForUpdate.add(dataObj.getId());
        }
    }

    public void deleteDataObjs(List<AbstractVehicle> obsMarkedForDelete) {
        for (AbstractVehicle objForDel : obsMarkedForDelete) {
            long idObjForDel = objForDel.getId();
            if (idObjForDel > 0) {
                dataObjIdsForDelete.add(idObjForDel);
            }
        }
        dataObjStorage.removeAll(obsMarkedForDelete);
    }
}
