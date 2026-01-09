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
package home.file.json_yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import home.Storage;
import home.file.Tag;
import home.model.AbstractVehicle;

public final class YamlExporter extends AbstractJsonYamlExporter {

    private static final int INDENT = 4;
    private static final int INDICATOR_INDENT = 2;

    @Override
    public String exportAllDataObjsToString() {
        var convertedDataObjs = new ArrayList<Map<String, String>>();
        for (AbstractVehicle dataObj : Storage.INSTANCE.getAll()) {
            convertedDataObjs.add(convertDataObjToMap(dataObj));
        }

        var dataMap = new HashMap<String, Object>();
        dataMap.put(Tag.VEHICLES.getTagName(), convertedDataObjs);

        var options = new DumperOptions();
        options.setIndent(INDENT);
        options.setIndicatorIndent(INDICATOR_INDENT);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        var yaml = new Yaml(options);
        String dataObjsInYamlFormat = yaml.dump(dataMap);
        dataObjsInYamlFormat = removeQuotesInValue(dataObjsInYamlFormat);
        dataObjsInYamlFormat = prettyPrintDashOffsets(dataObjsInYamlFormat);
        return dataObjsInYamlFormat;
    }

    private String removeQuotesInValue(String str) {
        return str.replace("'", "");
    }

    private String prettyPrintDashOffsets(String str) {
        String baseDashOffset = " ".repeat(INDICATOR_INDENT) + "-";
        String prettyDashOffset = baseDashOffset + "\n" + " ".repeat(INDENT - 1);
        return str.replace(baseDashOffset, prettyDashOffset);
    }
}