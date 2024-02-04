package home.file.ser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.Storage;
import home.file.IExporter;
import home.utils.LogUtils;

public final class BserExporter implements IExporter {

    private static final Logger LOG = LoggerFactory.getLogger(BserExporter.class);

    @Override
    public String exportAllDataObjsToString() {
        try (var byteArrOutputStream = new ByteArrayOutputStream();
                var objOutputStream = new ObjectOutputStream(byteArrOutputStream)) {
            objOutputStream.writeObject(Storage.INSTANCE.getAll());
            return Base64.getEncoder().encodeToString(byteArrOutputStream.toByteArray());
        } catch (IOException e) {
            throw LogUtils.logAndCreateIllegalStateException("BSER export converter error", LOG, e);
        }
    }
}