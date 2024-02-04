package home.file.ser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.model.AbstractVehicle;
import home.utils.LogUtils;

public final class SerImporter extends AbstractSerImporter {

    private static final Logger LOG = LoggerFactory.getLogger(SerImporter.class);

    private static SerImporter instance;

    private SerImporter() {
    }

    private static SerImporter getInstance() {
        if (instance == null) {
            instance = new SerImporter();
        }
        return instance;
    }

    public static List<AbstractVehicle> importDataObjsFromFile(File file) {
        return getInstance().importFromFile(file);
    }

    @Override
    protected List<AbstractVehicle> importFromFile(File file) {
        try (var fileInputStream = new FileInputStream(file);
                var bufInputStream = new BufferedInputStream(fileInputStream);
                var objInputStream = new ObjectInputStream(bufInputStream)) {
            return readDataObjs(objInputStream);
        } catch (ClassNotFoundException e) {
            throw LogUtils.logAndCreateIllegalStateException(
                    "Class of a serialized object from %s cannot befound.".formatted(file.getAbsolutePath()),
                    LOG, e);
        } catch (IOException e) {
            throw LogUtils.logAndCreateIllegalStateException("SER import error", LOG, e);
        }
    }
}