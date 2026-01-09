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
