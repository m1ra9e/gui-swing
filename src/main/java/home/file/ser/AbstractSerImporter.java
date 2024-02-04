package home.file.ser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import home.file.IImporter;
import home.model.AbstractVehicle;

abstract sealed class AbstractSerImporter
        implements IImporter permits BserImporter, SerImporter {

    protected List<AbstractVehicle> readDataObjs(ObjectInputStream objInputStream)
            throws ClassNotFoundException, IOException {
        @SuppressWarnings("unchecked")
        var dataObjs = (List<AbstractVehicle>) objInputStream.readObject();
        checkListObjTypes(dataObjs);
        return dataObjs;
    }

    private void checkListObjTypes(List<AbstractVehicle> dataObjs) {
        // empty loop checks the type of each object:
        // if the list contains objects of other types,
        // then the ClassCastException will be thrown
        for (AbstractVehicle dataObj : dataObjs) {
            // empty loop
        }
    }
}
