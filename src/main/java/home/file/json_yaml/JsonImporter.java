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

    private static JsonImporter instance;

    private JsonImporter() {
    }

    private static JsonImporter getInstance() {
        if (instance == null) {
            instance = new JsonImporter();
        }
        return instance;
    }

    public static List<AbstractVehicle> importDataObjsFromFile(File file) {
        return getInstance().importFromFile(file);
    }

    @Override
    protected List<AbstractVehicle> importFromFile(File file) {
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