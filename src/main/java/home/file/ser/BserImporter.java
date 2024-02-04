package home.file.ser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.file.FileHandler;
import home.model.AbstractVehicle;
import home.utils.LogUtils;

public final class BserImporter extends AbstractSerImporter {

    private static final Logger LOG = LoggerFactory.getLogger(BserImporter.class);

    private static BserImporter instance;

    private BserImporter() {
    }

    private static BserImporter getInstance() {
        if (instance == null) {
            instance = new BserImporter();
        }
        return instance;
    }

    public static List<AbstractVehicle> importDataObjsFromFile(File file) {
        return getInstance().importFromFile(file);
    }

    @Override
    protected List<AbstractVehicle> importFromFile(File file) {
        // StandardCharsets.ISO_8859_1 because of it used in
        // Base64 logic for encode and decode operations.
        String serializedDataObjsStr = FileHandler
                .readStringFromFile(file.getAbsolutePath(), StandardCharsets.ISO_8859_1);

        byte[] serializedDataObjsBytes = Base64.getDecoder().decode(serializedDataObjsStr);
        try (var byteArrInputStream = new ByteArrayInputStream(serializedDataObjsBytes);
                var objInputStream = new ObjectInputStream(byteArrInputStream)) {
            return readDataObjs(objInputStream);
        } catch (ClassNotFoundException e) {
            throw LogUtils.logAndCreateIllegalStateException(
                    "Class of a serialized object from %s cannot befound.".formatted(file.getAbsolutePath()),
                    LOG, e);
        } catch (IOException e) {
            throw LogUtils.logAndCreateIllegalStateException(
                    "Error while reading bser file : " + file.getAbsolutePath(),
                    LOG, e);
        }
    }
}