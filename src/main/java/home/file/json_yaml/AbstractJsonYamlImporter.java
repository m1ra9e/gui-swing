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
package home.file.json_yaml;

import java.util.Map;
import java.util.Map.Entry;

import home.file.IImporter;
import home.file.Tag;
import home.model.AbstractVehicle;
import home.model.AbstractVehicleWithTrailer;
import home.model.Car;
import home.model.Motorcycle;
import home.model.Truck;
import home.model.VehicleType;
import home.utils.Utils;

abstract sealed class AbstractJsonYamlImporter
implements IImporter permits JsonImporter, YamlImporter {

    protected void checkCountOfRootTags(int countOfRootTags) {
        if (countOfRootTags != 1) {
            throw new IllegalArgumentException("Incorrect count of root tags.");
        }
    }

    protected void checkRootTagName(String rootTagName) {
        if (!Tag.VEHICLES.getTagName().equals(rootTagName)) {
            throw new IllegalArgumentException("Unknown root tag name : " + rootTagName);
        }
    }

    protected AbstractVehicle convertToDataObj(Map<String, String> rawDataStringMap) {
        String type = removeRequiredParam(Tag.TYPE, rawDataStringMap);
        VehicleType vehicleType = VehicleType.getVehicleType(type);

        AbstractVehicle dataObj = switch (vehicleType) {
            case CAR -> new Car();
            case TRUCK -> new Truck();
            case MOTORCYCLE -> new Motorcycle();
        };

        for (Entry<String, String> tagData : rawDataStringMap.entrySet()) {
            String tagName = tagData.getKey();
            String tagValue = tagData.getValue();

            Tag tag = Tag.getTag(tagName, "Incorrect tag name : %s");

            switch (tag) {
                case COLOR -> dataObj.setColor(tagValue);
                case NUMBER -> dataObj.setNumber(tagValue);
                case DATE -> dataObj.setDateTime(Utils.getLongFromFormattedDate(tagValue));
                case HAS_TRAILER -> {
                    if (vehicleType.in(VehicleType.CAR, VehicleType.TRUCK)) {
                        boolean hasTrailer = Boolean.parseBoolean(tagValue);
                        ((AbstractVehicleWithTrailer) dataObj).setHasTrailer(hasTrailer);
                    }
                }
                case IS_TRANSPORTS_PASSENGERS -> {
                    if (VehicleType.CAR == vehicleType) {
                        boolean isTransportsPassengers = Boolean.parseBoolean(tagValue);
                        ((Car) dataObj).setTransportsPassengers(isTransportsPassengers);
                    }
                }
                case IS_TRANSPORTS_CARGO -> {
                    if (VehicleType.TRUCK == vehicleType) {
                        boolean isTransportsCargo = Boolean.parseBoolean(tagValue);
                        ((Truck) dataObj).setTransportsCargo(isTransportsCargo);
                    }
                }
                case HAS_CRADLE -> {
                    if (VehicleType.MOTORCYCLE == vehicleType) {
                        boolean hasCradle = Boolean.parseBoolean(tagValue);
                        ((Motorcycle) dataObj).setHasCradle(hasCradle);
                    }
                }
                default -> throw new IllegalArgumentException(
                        "There is no processing for " + tagName);
            }
        }

        return dataObj;
    }

    private String removeRequiredParam(Tag tag, Map<String, String> map) {
        String tagName = tag.getTagName();
        String tagValue = map.remove(tagName);
        if (tagValue != null) {
            return tagValue;
        }
        throw new IllegalThreadStateException("There is no required tag : " + tagName);
    }
}
