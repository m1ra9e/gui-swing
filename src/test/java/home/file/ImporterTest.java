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
