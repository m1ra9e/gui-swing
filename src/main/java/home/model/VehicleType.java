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
package home.model;

public enum VehicleType {

    CAR("car"),
    MOTORCYCLE("motorcycle"),
    TRUCK("truck");

    private final String type;

    private VehicleType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static VehicleType getVehicleType(String type) {
        String typeFormatted = type.strip();
        for (VehicleType vehicleType : VehicleType.values()) {
            if (typeFormatted.equalsIgnoreCase(vehicleType.getType())) {
                return vehicleType;
            }
        }

        throw new IllegalArgumentException("Wrong vehicle type received : " + type);
    }

    public boolean in(VehicleType... vehicleTypes) {
        for (var vehicleType : vehicleTypes) {
            if (this == vehicleType) {
                return true;
            }
        }
        return false;
    }
}
