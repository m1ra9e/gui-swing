package home;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import home.gui.Gui;
import home.models.AbstractVehicle;

public class Storage {

    public static final int NO_ROW_IS_SELECTED = -1;

    private static final List<AbstractVehicle> DATA_OBJS = new ArrayList<>();
    private static final Set<Long> DATA_OBJ_IDS_FOR_DELETE = new HashSet<>();

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
        DATA_OBJ_IDS_FOR_DELETE.clear();
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

    public Long[] getIdsForDelete() {
        return DATA_OBJ_IDS_FOR_DELETE.stream().map(id -> Long.valueOf(id))
                .toArray(Long[]::new);
    }

    public void updateStorage(AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        if (NO_ROW_IS_SELECTED == tblRowOfSelectedDataObj) {
            DATA_OBJS.add(dataObj);
        } else {
            DATA_OBJS.set(tblRowOfSelectedDataObj, dataObj);
        }
    }

    public void deleteObjects(List<AbstractVehicle> obsMarkedForDelete) {
        for (AbstractVehicle objForDel : obsMarkedForDelete) {
            long idObjForDel = objForDel.getId();
            if (idObjForDel > 0) {
                DATA_OBJ_IDS_FOR_DELETE.add(idObjForDel);
            }
        }
        DATA_OBJS.removeAll(obsMarkedForDelete);
    }
}
