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

import home.file.IImporter;
import home.model.AbstractVehicle;
import home.model.AbstractVehicleWithTrailer;
import home.model.Car;
import home.model.Motorcycle;
import home.model.Truck;
import home.model.VehicleType;
import home.utils.LogUtils;
import home.utils.Utils;

public final class CsvImporter implements IImporter {

    private static final Logger LOG = LoggerFactory.getLogger(CsvImporter.class);

    @Override
    public List<AbstractVehicle> importDataObjsFromFile(File file) {
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

    private List<AbstractVehicle> parse(List<String[]> rawDataObjs) {
        checkElementsCountInRawDataObjs(rawDataObjs);

        var dataObjs = new ArrayList<AbstractVehicle>();
        for (String[] rawDataObj : getRawDataObjsWithoutHeader(rawDataObjs)) {
            dataObjs.add(convertToDataObj(rawDataObj));
        }
        return dataObjs;
    }

    private void checkElementsCountInRawDataObjs(List<String[]> rawDataObjs) {
        for (String[] rawDataObj : rawDataObjs) {
            if (CsvConst.CSV_ROW_SIZE != rawDataObj.length) {
                throw new IllegalArgumentException("Incorrect count of elements in : [%s]"
                        .formatted(String.join(", ", rawDataObj)));
            }
        }
    }

    private List<String[]> getRawDataObjsWithoutHeader(List<String[]> rawDataObjs) {
        String[] header = rawDataObjs.get(0);
        return Arrays.equals(header, CsvConst.CSV_HEADER)
                ? rawDataObjs.subList(1, rawDataObjs.size())
                        : rawDataObjs;
    }

    private AbstractVehicle convertToDataObj(String[] rawDataObj) {
        String type = rawDataObj[CsvConst.TYPE_IDX];
        VehicleType vehicleType = VehicleType.getVehicleType(type);

        AbstractVehicle dataObj = switch (vehicleType) {
            case CAR -> new Car();
            case TRUCK -> new Truck();
            case MOTORCYCLE -> new Motorcycle();
        };

        for (int tagIdx = CsvConst.COLOR_IDX; CsvConst.HAS_CRADLE_IDX >= tagIdx; tagIdx++) {
            String value = rawDataObj[tagIdx];
            switch (tagIdx) {
                case CsvConst.COLOR_IDX -> dataObj.setColor(value);
                case CsvConst.NUMBER_IDX -> dataObj.setNumber(value);
                case CsvConst.DATE_IDX -> dataObj.setDateTime(Utils.getLongFromFormattedDate(value));
                case CsvConst.HAS_TRAILER_IDX -> {
                    if (vehicleType.in(VehicleType.CAR, VehicleType.TRUCK)) {
                        boolean hasTrailer = Boolean.parseBoolean(value);
                        ((AbstractVehicleWithTrailer) dataObj).setHasTrailer(hasTrailer);
                    }
                }
                case CsvConst.IS_TRANSPORTS_PASSENGERS_IDX -> {
                    if (VehicleType.CAR == vehicleType) {
                        boolean isTransportsPassengers = Boolean.parseBoolean(value);
                        ((Car) dataObj).setTransportsPassengers(isTransportsPassengers);
                    }
                }
                case CsvConst.IS_TRANSPORTS_CARGO_IDX -> {
                    if (VehicleType.TRUCK == vehicleType) {
                        boolean isTransportsCargo = Boolean.parseBoolean(value);
                        ((Truck) dataObj).setTransportsCargo(isTransportsCargo);
                    }
                }
                case CsvConst.HAS_CRADLE_IDX -> {
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
}