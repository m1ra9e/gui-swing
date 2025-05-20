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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import home.model.AbstractVehicle;
import home.utils.LogUtils;

public final class YamlImporter extends AbstractJsonYamlImporter {

    private static final Logger LOG = LoggerFactory.getLogger(YamlImporter.class);

    @Override
    public List<AbstractVehicle> importDataObjsFromFile(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            var yaml = new Yaml();
            Map<String, Object> allData = yaml.load(inputStream);
            List<AbstractVehicle> dataObjs = parse(allData);
            return dataObjs;
        } catch (FileNotFoundException e) {
            throw LogUtils.logAndCreateIllegalStateException(
                    "Can't find yaml file for import data : " + file.getAbsolutePath(),
                    LOG, e);
        } catch (IOException e) {
            throw LogUtils.logAndCreateIllegalStateException(
                    "Error while reading yaml file : " + file.getAbsolutePath(),
                    LOG, e);
        }
    }

    private List<AbstractVehicle> parse(Map<String, Object> allData) {
        checkCountOfRootTags(allData.size());

        Entry<String, Object> rootTagData = allData.entrySet().iterator().next();
        String rootTagName = rootTagData.getKey();

        checkRootTagName(rootTagName);

        var dataObjs = new ArrayList<AbstractVehicle>();

        Object rootTagValue = rootTagData.getValue();
        List<?> rawDataObjsList = castToList(rootTagValue, rootTagName);
        for (Object rawDataObj : rawDataObjsList) {
            Map<?, ?> rawDataObjMap = castToMap(rawDataObj, rootTagName);
            Map<String, String> rawDataStringMap = convertToStringMap(rawDataObjMap);
            dataObjs.add(convertToDataObj(rawDataStringMap));
        }

        return dataObjs;
    }

    private List<?> castToList(Object obj, String tagName) {
        if (obj instanceof List<?> list) {
            return list;
        }
        throw new IllegalArgumentException("Error while parse values of [%s]"
                .formatted(tagName));
    }

    private Map<?, ?> castToMap(Object obj, String tagName) {
        if (obj instanceof Map<?, ?> map) {
            return map;
        }
        throw new IllegalArgumentException("Error while parse value of [%s]"
                .formatted(tagName));
    }

    private Map<String, String> convertToStringMap(Map<?, ?> rawDataObjMap) {
        return rawDataObjMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> convertToString(entry.getKey()),
                        entry -> convertToString(entry.getValue())));
    }

    private String convertToString(Object obj) {
        return obj instanceof String ? (String) obj : obj.toString();
    }
}