package home.models;

import java.util.Objects;

public abstract class AbstractVehicle {

    private long id;
    private VehicleType type;
    private String color;
    private String number;
    private long dateTime;

    private boolean isMarkedForDelete;

    public AbstractVehicle() {
        this.type = getInitializedType();
    }

    public abstract VehicleType getInitializedType();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isMarkedForDelete() {
        return isMarkedForDelete;
    }

    public void setMarkedForDelete(boolean isMarkedForDelete) {
        this.isMarkedForDelete = isMarkedForDelete;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, dateTime, id, isMarkedForDelete, number, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof AbstractVehicle)) {
            return false;
        }

        AbstractVehicle other = (AbstractVehicle) obj;
        return Objects.equals(color, other.color)
                && dateTime == other.dateTime
                && id == other.id
                && isMarkedForDelete == other.isMarkedForDelete
                && Objects.equals(number, other.number)
                && type == other.type;
    }
}
