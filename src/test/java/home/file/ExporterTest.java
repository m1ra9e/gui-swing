package home.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import home.file.json_yaml.YamlExporter;

final class ExporterTest extends AbstractFileTest {

    @Test
    void exportDataObjsToStringTest() throws Exception {
        Path filePath = getFilePath("data_objs.yaml");
        String expected = FileHandler.readStringFromFile(filePath.toString(), null);

        String actual = YamlExporter.exportAllDataObjsToString();

        assertEquals(expected.strip(), actual.strip());
    }
}
