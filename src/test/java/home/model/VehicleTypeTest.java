package home.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import home.model.VehicleType;

final class VehicleTypeTest {

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
    void existingType(String type) {
        assertNotNull(VehicleType.getVehicleType(type));
    }

    @Test
    void notExistedType() {
        assertNull(VehicleType.getVehicleType("notExistedType"));
    }

    @Disabled("Just to show the disable function in the test")
    @Test
    void disabledTest() {
        String expected = "some value";
        String actual = "some value";
        assertEquals(expected, actual);
    }
}
