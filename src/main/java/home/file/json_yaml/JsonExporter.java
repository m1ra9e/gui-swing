/*******************************************************************************
 * Copyright 2021-2024 Lenar Shamsutdinov
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
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import home.Storage;
import home.file.Tag;
import home.model.AbstractVehicle;
import home.utils.LogUtils;

public final class JsonExporter extends AbstractJsonYamlExporter {

    private static final Logger LOG = LoggerFactory.getLogger(JsonExporter.class);

    @Override
    public String exportAllDataObjsToString() {
        try {
            var convertedDataObjs = new ArrayList<Map<String, String>>();
            for (AbstractVehicle dataObj : Storage.INSTANCE.getAll()) {
                convertedDataObjs.add(convertDataObjToMap(dataObj));
            }

            var dataMap = new HashMap<String, List<Map<String, String>>>();
            dataMap.put(Tag.VEHICLES.getTagName(), convertedDataObjs);

            ObjectMapper objectMapper = new ObjectMapper();
            String dataObjsInJsonFormat = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dataMap);
            return dataObjsInJsonFormat;
        } catch (JsonProcessingException e) {
            throw LogUtils.logAndCreateIllegalStateException("JSON converter error", LOG, e);
        }
    }
}
