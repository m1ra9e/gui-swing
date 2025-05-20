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
package home.file.csv;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import home.Storage;
import home.file.IExporter;
import home.model.AbstractVehicle;
import home.model.AbstractVehicleWithTrailer;
import home.model.Car;
import home.model.Motorcycle;
import home.model.Truck;
import home.model.VehicleType;
import home.utils.LogUtils;
import home.utils.Utils;

public final class CsvExporter implements IExporter {

    private static final Logger LOG = LoggerFactory.getLogger(CsvExporter.class);

    private static final char CSV_EMPTY_QUOTE_CHAR = Character.MIN_VALUE;
    private static final char CSV_PARAMS_SEPARATOR = ',';

    private static final String FALSE = Boolean.FALSE.toString();

    @Override
    public String exportAllDataObjsToString() {
        List<AbstractVehicle> dataObjStorage = Storage.INSTANCE.getAll();

        var convertedDataObjs = new ArrayList<String[]>(dataObjStorage.size() + 1);
        convertedDataObjs.add(CsvConst.CSV_HEADER);
        for (AbstractVehicle dataObj : dataObjStorage) {
            convertedDataObjs.add(convertDataObjToArray(dataObj));
        }

        try (var stringWriter = new StringWriter();
                ICSVWriter csvWriter = new CSVWriterBuilder(stringWriter)
                        .withQuoteChar(CSV_EMPTY_QUOTE_CHAR)
                        .withSeparator(CSV_PARAMS_SEPARATOR).build()) {
            csvWriter.writeAll(convertedDataObjs);
            String dataObjsInCsvFormat = stringWriter.toString();
            return dataObjsInCsvFormat;
        } catch (IOException e) {
            throw LogUtils.logAndCreateIllegalStateException("CSV converter error", LOG, e);
        }
    }

    private String[] convertDataObjToArray(AbstractVehicle dataObj) {
        var array = new String[CsvConst.CSV_ROW_SIZE];

        VehicleType type = dataObj.getType();

        array[CsvConst.TYPE_IDX] = type.getType();
        array[CsvConst.COLOR_IDX] = dataObj.getColor();
        array[CsvConst.NUMBER_IDX] = dataObj.getNumber();
        array[CsvConst.DATE_IDX] = Utils.getFormattedDate(dataObj.getDateTime());

        String hasTrailerStr = FALSE;
        if (type.in(VehicleType.CAR, VehicleType.TRUCK)) {
            hasTrailerStr = Boolean.toString(((AbstractVehicleWithTrailer) dataObj).hasTrailer());
        }
        array[CsvConst.HAS_TRAILER_IDX] = hasTrailerStr;

        String isTransportsPassengersStr = FALSE;
        if (VehicleType.CAR == type) {
            isTransportsPassengersStr = Boolean.toString(((Car) dataObj).isTransportsPassengers());
        }
        array[CsvConst.IS_TRANSPORTS_PASSENGERS_IDX] = isTransportsPassengersStr;

        String isTransportsCargoStr = FALSE;
        if (VehicleType.TRUCK == type) {
            isTransportsCargoStr = Boolean.toString(((Truck) dataObj).isTransportsCargo());
        }
        array[CsvConst.IS_TRANSPORTS_CARGO_IDX] = isTransportsCargoStr;

        String hasCradle = FALSE;
        if (VehicleType.MOTORCYCLE == type) {
            hasCradle = Boolean.toString(((Motorcycle) dataObj).hasCradle());
        }
        array[CsvConst.HAS_CRADLE_IDX] = hasCradle;

        return array;
    }
}
