package home.file;

import java.io.File;
import java.util.List;

import home.model.AbstractVehicle;

public interface IImporter {

    List<AbstractVehicle> importDataObjsFromFile(File file);
}
