package home.file;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import home.file.csv.CsvImporter;
import home.file.json_yaml.JsonImporter;
import home.file.json_yaml.YamlImporter;
import home.file.ser.BserImporter;
import home.file.ser.SerImporter;
import home.file.xml.XmlImporter;
import home.gui.component.CustomJFileChooserImpExp.DataFormat;
import home.model.AbstractVehicle;

final class ImporterTest extends AbstractFileTest {

    @ParameterizedTest
    @EnumSource(DataFormat.class)
    void importDataObjFromFileTest(DataFormat dataFormat) throws Exception {
        List<AbstractVehicle> expected = AbstractFileTest.getTestDataObjs();

        Path filePath = getFilePath(dataFormat.getExtension());
        List<AbstractVehicle> actual = getImporter(dataFormat)
                .importDataObjsFromFile(filePath.toFile());

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    private IImporter getImporter(DataFormat dataFormat) {
        return switch (dataFormat) {
            case XML -> new XmlImporter();
            case YAML -> new YamlImporter();
            case JSON -> new JsonImporter();
            case CSV -> new CsvImporter();
            case BSER -> new BserImporter();
            case SER -> new SerImporter();
        };
    }
}
