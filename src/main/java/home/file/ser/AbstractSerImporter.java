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
package home.file.ser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import home.file.IImporter;
import home.model.AbstractVehicle;

abstract sealed class AbstractSerImporter
        implements IImporter permits BserImporter, SerImporter {

    protected List<AbstractVehicle> readDataObjs(ObjectInputStream objInputStream)
            throws ClassNotFoundException, IOException {
        @SuppressWarnings("unchecked")
        var dataObjs = (List<AbstractVehicle>) objInputStream.readObject();
        checkListObjTypes(dataObjs);
        return dataObjs;
    }

    private void checkListObjTypes(List<AbstractVehicle> dataObjs) {
        // empty loop checks the type of each object:
        // if the list contains objects of other types,
        // then the ClassCastException will be thrown
        for (AbstractVehicle dataObj : dataObjs) {
            // empty loop
        }
    }
}
