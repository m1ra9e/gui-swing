package home.models;

import java.util.Objects;

public abstract class AbstractVehicleWithTrailer extends AbstractVehicle {

    private boolean hasTrailer;

    public boolean hasTrailer() {
        return hasTrailer;
    }

    public void setHasTrailer(boolean hasTrailer) {
        this.hasTrailer = hasTrailer;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(hasTrailer);
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

        if (!(obj instanceof AbstractVehicleWithTrailer)) {
            return false;
        }

        AbstractVehicleWithTrailer other = (AbstractVehicleWithTrailer) obj;
        return hasTrailer == other.hasTrailer;
    }
}
