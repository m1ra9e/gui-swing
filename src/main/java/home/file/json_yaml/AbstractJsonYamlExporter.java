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
package home.file.json_yaml;

import java.util.LinkedHashMap;
import java.util.Map;

import home.file.IExporter;
import home.file.Tag;
import home.model.AbstractVehicle;
import home.model.AbstractVehicleWithTrailer;
import home.model.Car;
import home.model.Motorcycle;
import home.model.Truck;
import home.model.VehicleType;
import home.utils.Utils;

abstract sealed class AbstractJsonYamlExporter
        implements IExporter permits JsonExporter, YamlExporter {

    protected Map<String, String> convertDataObjToMap(AbstractVehicle dataObj) {
        var map = new LinkedHashMap<String, String>();

        VehicleType type = dataObj.getType();

        map.put(Tag.TYPE.getTagName(), type.getType());
        map.put(Tag.COLOR.getTagName(), dataObj.getColor());
        map.put(Tag.NUMBER.getTagName(), dataObj.getNumber());
        map.put(Tag.DATE.getTagName(), Utils.getFormattedDate(dataObj.getDateTime()));

        if (type.in(VehicleType.CAR, VehicleType.TRUCK)) {
            map.put(Tag.HAS_TRAILER.getTagName(),
                    Boolean.toString(((AbstractVehicleWithTrailer) dataObj).hasTrailer()));
        }

        if (VehicleType.CAR == type) {
            map.put(Tag.IS_TRANSPORTS_PASSENGERS.getTagName(),
                    Boolean.toString(((Car) dataObj).isTransportsPassengers()));
        }

        if (VehicleType.TRUCK == type) {
            map.put(Tag.IS_TRANSPORTS_CARGO.getTagName(),
                    Boolean.toString(((Truck) dataObj).isTransportsCargo()));
        }

        if (VehicleType.MOTORCYCLE == type) {
            map.put(Tag.HAS_CRADLE.getTagName(),
                    Boolean.toString(((Motorcycle) dataObj).hasCradle()));
        }

        return map;
    }
}
