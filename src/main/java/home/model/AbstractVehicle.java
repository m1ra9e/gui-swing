package home.model;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.TimeZone;

public abstract sealed class AbstractVehicle
        implements Serializable permits AbstractVehicleWithTrailer,Motorcycle {

    private static final long serialVersionUID = 4228103618146673801L;

    private static final char SPACE = ' ';

    private transient long id; // transient for work save mechanism with *.ser, *.bser
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

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("Vehicle");
        if (id != 0) {
            sb.append(SPACE).append("[id=").append(id).append(']');
        }
        sb.append(SPACE).append(':').append(SPACE).append(color).append(SPACE).append(type)
                .append(SPACE).append("with number").append(SPACE).append(number);

        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime),
                TimeZone.getDefault().toZoneId());

        sb.append(SPACE).append('(').append(time).append(')');

        return sb.toString();
    }
}
