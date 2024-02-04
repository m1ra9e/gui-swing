package home.file;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import home.file.json_yaml.YamlImporter;
import home.model.AbstractVehicle;

final class ImporterTest extends AbstractFileTest {

    @Test
    void importDataObjFromFileTest() throws Exception {
        List<AbstractVehicle> expected = AbstractFileTest.getTestDataObjs();

        Path filePath = getFilePath("data_objs.yaml");
        List<AbstractVehicle> actual = YamlImporter.importDataObjsFromFile(filePath.toFile());

        assertArrayEquals(expected.toArray(), actual.toArray());
    }
}
