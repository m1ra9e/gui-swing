package home;

import java.util.ArrayList;
import java.util.List;

import home.gui.Gui;
import home.models.AbstractVehicle;

public class Storage {

    private static final List<AbstractVehicle> DATA_OBJS = new ArrayList<>();

    private static Storage instance;

    private Storage() {
    }

    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    public void refresh(List<AbstractVehicle> dataObjs) {
        DATA_OBJS.clear();
        DATA_OBJS.addAll(dataObjs);
        Gui.getInstance().refreshTable();
    }

    public List<AbstractVehicle> getAll() {
        return DATA_OBJS;
    }

    public AbstractVehicle get(int row) {
        return DATA_OBJS.get(row);
    }
}
