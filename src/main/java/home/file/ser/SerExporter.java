package home.file.ser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.Storage;
import home.file.IExporter;
import home.utils.LogUtils;

public final class SerExporter implements IExporter {

    private static final Logger LOG = LoggerFactory.getLogger(SerExporter.class);

    public void exportAllDataObjsToFile(File file) {
        try (var fileOutputStream = new FileOutputStream(file);
                var bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                var objOutputStream = new ObjectOutputStream(bufferedOutputStream)) {
            objOutputStream.writeObject(Storage.INSTANCE.getAll());
            objOutputStream.flush();
        } catch (IOException e) {
            throw LogUtils.logAndCreateIllegalStateException("SER export error", LOG, e);
        }
    }

    @Override
    public String exportAllDataObjsToString() {
        throw LogUtils.logAndCreateIllegalStateException(
                "SER export error: unimplemented method,"
                        + " use 'exportAllDataObjsToFile(file file)' instead",
                LOG, null);
    }
}