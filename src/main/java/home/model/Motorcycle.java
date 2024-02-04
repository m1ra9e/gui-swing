package home.model;

import java.util.Objects;

public final class Motorcycle extends AbstractVehicle {

    private static final long serialVersionUID = 4323155472149566955L;

    private boolean hasCradle;

    @Override
    public VehicleType getInitializedType() {
        return VehicleType.MOTORCYCLE;
    }

    public boolean hasCradle() {
        return hasCradle;
    }

    public void setHasCradle(boolean hasCradle) {
        this.hasCradle = hasCradle;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(hasCradle);
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

        if (!(obj instanceof Motorcycle)) {
            return false;
        }

        Motorcycle other = (Motorcycle) obj;
        return hasCradle == other.hasCradle;
    }
}