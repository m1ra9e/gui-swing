/*******************************************************************************
 * Copyright 2021-2025 Lenar Shamsutdinov
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import home.model.AbstractVehicle;
import home.utils.LogUtils;

public final class JsonImporter extends AbstractJsonYamlImporter {

    private static final Logger LOG = LoggerFactory.getLogger(JsonImporter.class);

    private static final TypeReference<Map<String, List<Map<String, String>>>> TYPE_REFERENCE = new TypeReference<>() {
    };

    @Override
    public List<AbstractVehicle> importDataObjsFromFile(File file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, List<Map<String, String>>> allData = objectMapper.readValue(file, TYPE_REFERENCE);
            List<AbstractVehicle> dataObjs = parse(allData);
            return dataObjs;
        } catch (IOException e) {
            throw LogUtils.logAndCreateIllegalStateException(
                    "Error while reading json file : " + file.getAbsolutePath(),
                    LOG, e);
        }
    }

    private List<AbstractVehicle> parse(Map<String, List<Map<String, String>>> allData) {
        checkCountOfRootTags(allData.size());

        Entry<String, List<Map<String, String>>> rootTagData = allData.entrySet().iterator().next();
        String rootTagName = rootTagData.getKey();

        checkRootTagName(rootTagName);

        var dataObjs = new ArrayList<AbstractVehicle>();

        List<Map<String, String>> rootTagValue = rootTagData.getValue();
        for (Map<String, String> rawDataStringMap : rootTagValue) {
            dataObjs.add(convertToDataObj(rawDataStringMap));
        }

        return dataObjs;
    }
}