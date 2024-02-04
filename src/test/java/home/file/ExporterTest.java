package home.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import home.file.csv.CsvExporter;
import home.file.json_yaml.JsonExporter;
import home.file.json_yaml.YamlExporter;
import home.file.ser.BserExporter;
import home.file.ser.SerExporter;
import home.file.xml.XmlExporter;
import home.gui.component.CustomJFileChooserImpExp.DataFormat;

final class ExporterTest extends AbstractFileTest {

    @ParameterizedTest
    @EnumSource(DataFormat.class)
    void exportDataObjsToStringTest(DataFormat dataFormat) throws Exception {
        if (DataFormat.SER == dataFormat) {
            return;
        }

        Path filePath = getFilePath(dataFormat.getExtension());
        String expected = FileHandler.readStringFromFile(filePath.toString(), null);

        String actual = getExporter(dataFormat).exportAllDataObjsToString();

        assertEquals(expected.strip(), actual.strip());
    }

    private IExporter getExporter(DataFormat dataFormat) {
        return switch (dataFormat) {
            case XML -> new XmlExporter();
            case YAML -> new YamlExporter();
            case JSON -> new JsonExporter();
            case CSV -> new CsvExporter();
            case BSER -> new BserExporter();
            case SER -> new SerExporter();
        };
    }
}
