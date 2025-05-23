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
package home.model;

import java.util.Objects;

public final class Truck extends AbstractVehicleWithTrailer {

    private static final long serialVersionUID = -6141015683953451281L;

    private boolean isTransportsCargo;

    @Override
    public VehicleType getInitializedType() {
        return VehicleType.TRUCK;
    }

    public boolean isTransportsCargo() {
        return isTransportsCargo;
    }

    public void setTransportsCargo(boolean isTransportsCargo) {
        this.isTransportsCargo = isTransportsCargo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(isTransportsCargo);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof Truck)) {
            return false;
        }

        Truck other = (Truck) obj;
        return isTransportsCargo == other.isTransportsCargo;
    }
}
