package home.file.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import home.model.AbstractVehicle;
import home.model.AbstractVehicleWithTrailer;
import home.model.Car;
import home.model.Motorcycle;
import home.model.Truck;
import home.model.VehicleType;
import home.utils.LogUtils;
import home.utils.Utils;

public final class CsvImporter {

    private static final Logger LOG = LoggerFactory.getLogger(CsvImporter.class);

    public static List<AbstractVehicle> importDataObjsFromFile(File file) {
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            List<String[]> rawDataObjs = reader.readAll();
            List<AbstractVehicle> dataObjs = parse(rawDataObjs);
            return dataObjs;
        } catch (FileNotFoundException e) {
            throw LogUtils.logAndCreateIllegalStateException(
                    "Can't find csv file for import data : " + file.getAbsolutePath(),
                    LOG, e);
        } catch (IOException e) {
            throw LogUtils.logAndCreateIllegalStateException(
                    "Error while reading csv file : " + file.getAbsolutePath(),
                    LOG, e);
        } catch (CsvException e) {
            throw LogUtils.logAndCreateIllegalStateException(
                    "Error while validate csv file : " + file.getAbsolutePath(),
                    LOG, e);
        }
    }

    private static List<AbstractVehicle> parse(List<String[]> rawDataObjs) {
        checkElementsCountInRawDataObjs(rawDataObjs);

        var dataObjs = new ArrayList<AbstractVehicle>();
        for (String[] rawDataObj : getRawDataObjsWithoutHeader(rawDataObjs)) {
            dataObjs.add(convertToDataObj(rawDataObj));
        }
        return dataObjs;
    }

    private static void checkElementsCountInRawDataObjs(List<String[]> rawDataObjs) {
        for (String[] rawDataObj : rawDataObjs) {
            if (ICsvConsts.CSV_ROW_SIZE != rawDataObj.length) {
                throw new IllegalArgumentException("Incorrect count of elements in : [%s]"
                        .formatted(String.join(", ", rawDataObj)));
            }
        }
    }

    private static List<String[]> getRawDataObjsWithoutHeader(List<String[]> rawDataObjs) {
        String[] header = rawDataObjs.get(0);
        return Arrays.equals(header, ICsvConsts.CSV_HEADER)
                ? rawDataObjs.subList(1, rawDataObjs.size())
                : rawDataObjs;
    }

    private static AbstractVehicle convertToDataObj(String[] rawDataObj) {
        String type = rawDataObj[ICsvConsts.TYPE_IDX];
        VehicleType vehicleType = VehicleType.getVehicleType(type);
        if (vehicleType == null) {
            throw new IllegalArgumentException("Wrong vehicle type received : " + type);
        }

        AbstractVehicle dataObj = switch (vehicleType) {
            case CAR -> new Car();
            case TRUCK -> new Truck();
            case MOTORCYCLE -> new Motorcycle();
        };

        for (int tagIdx = ICsvConsts.COLOR_IDX; ICsvConsts.HAS_CRADLE_IDX >= tagIdx; tagIdx++) {
            String value = rawDataObj[tagIdx];
            switch (tagIdx) {
                case ICsvConsts.COLOR_IDX -> dataObj.setColor(value);
                case ICsvConsts.NUMBER_IDX -> dataObj.setNumber(value);
                case ICsvConsts.DATE_IDX -> dataObj.setDateTime(Utils.getLongFromFormattedDate(value));
                case ICsvConsts.HAS_TRAILER_IDX -> {
                    if (vehicleType.in(VehicleType.CAR, VehicleType.TRUCK)) {
                        boolean hasTrailer = Boolean.parseBoolean(value);
                        ((AbstractVehicleWithTrailer) dataObj).setHasTrailer(hasTrailer);
                    }
                }
                case ICsvConsts.IS_TRANSPORTS_PASSENGERS_IDX -> {
                    if (VehicleType.CAR == vehicleType) {
                        boolean isTransportsPassengers = Boolean.parseBoolean(value);
                        ((Car) dataObj).setTransportsPassengers(isTransportsPassengers);
                    }
                }
                case ICsvConsts.IS_TRANSPORTS_CARGO_IDX -> {
                    if (VehicleType.TRUCK == vehicleType) {
                        boolean isTransportsCargo = Boolean.parseBoolean(value);
                        ((Truck) dataObj).setTransportsCargo(isTransportsCargo);
                    }
                }
                case ICsvConsts.HAS_CRADLE_IDX -> {
                    if (VehicleType.MOTORCYCLE == vehicleType) {
                        boolean hasCradle = Boolean.parseBoolean(value);
                        ((Motorcycle) dataObj).setHasCradle(hasCradle);
                    }
                }
                default -> throw new IllegalArgumentException(
                        "There is no processing for tag index " + tagIdx);
            }
        }

        return dataObj;
    }

    private CsvImporter() {
    }
}