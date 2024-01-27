package home.models;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TypeTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "car",
            "truck",
            "motorcycle",

            // check ignore case
            "CAR",
            "tRuck",
            "Motorcycle"
    })
    public void existingType(String type) {
        assertNotNull(VehicleType.getVehicleType(type));
    }

    @Test
    public void notExistedType() {
        assertNull(VehicleType.getVehicleType("notExistedType"));
    }
}
