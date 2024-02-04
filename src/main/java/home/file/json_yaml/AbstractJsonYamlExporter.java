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
