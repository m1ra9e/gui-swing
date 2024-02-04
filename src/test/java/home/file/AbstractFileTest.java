package home.file;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import home.Storage;
import home.model.AbstractVehicle;
import home.model.Car;
import home.model.Motorcycle;
import home.model.Truck;
import home.utils.Utils;

abstract sealed class AbstractFileTest permits ImporterTest, ExporterTest {

    private static final String FILE_NAME = "data_objs.%s";

    private static List<AbstractVehicle> dataObjs = new LinkedList<>();

    @BeforeAll
    static void fillStorage() {
        Storage.INSTANCE.initDataObjs(getTestDataObjs());
    }

    protected Path getFilePath(String extension) throws URISyntaxException {
        String fileName = FILE_NAME.formatted(extension);
        return Paths.get(getClass().getResource(fileName).toURI()).toAbsolutePath();
    }

    protected static List<AbstractVehicle> getTestDataObjs() {
        if (!dataObjs.isEmpty()) {
            return dataObjs;
        }

        var motorcycle = new Motorcycle();
        motorcycle.setColor("black");
        motorcycle.setNumber("1a");
        motorcycle.setDateTime(Utils.getLongFromFormattedDate("2020.01.23 | 10:00:00"));
        dataObjs.add(motorcycle);

        var car = new Car();
        car.setColor("yellow");
        car.setNumber("22b");
        car.setDateTime(Utils.getLongFromFormattedDate("2021.02.24 | 11:01:27"));
        car.setTransportsPassengers(true);
        dataObjs.add(car);

        var truck = new Truck();
        truck.setColor("green");
        truck.setNumber("333c");
        truck.setDateTime(Utils.getLongFromFormattedDate("2022.03.25 | 12:02:11"));
        truck.setHasTrailer(true);
        dataObjs.add(truck);

        return dataObjs;
    }

    @AfterAll
    static void cleanStorage() {
        Storage.INSTANCE.initDataObjs(Collections.emptyList());
    }
}
