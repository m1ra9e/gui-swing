package home;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import home.gui.Gui;
import home.models.AbstractVehicle;

public enum Storage {

    INSTANCE;

    public static final int NO_ROW_IS_SELECTED = -1;

    private final List<AbstractVehicle> dataObjStorage = new ArrayList<>();
    private final Set<Long> dataObjIdsForDelete = new HashSet<>();
    private final Set<Long> dataObjIdsForUpdate = new HashSet<>();

    public void refresh(List<AbstractVehicle> dataObjs) {
        dataObjIdsForDelete.clear();
        dataObjIdsForUpdate.clear();
        dataObjStorage.clear();
        dataObjStorage.addAll(dataObjs);
        Gui.INSTANCE.refreshTable();
    }

    public List<AbstractVehicle> getAll() {
        return dataObjStorage;
    }

    public AbstractVehicle get(int row) {
        return dataObjStorage.get(row);
    }

    public Long[] getIdsForDelete() {
        return dataObjIdsForDelete.stream().map(id -> Long.valueOf(id))
                .toArray(Long[]::new);
    }

    public Set<Long> getIdsForUpdate() {
        return dataObjIdsForUpdate;
    }

    public void updateStorage(AbstractVehicle dataObj, int tblRowOfSelectedDataObj) {
        if (NO_ROW_IS_SELECTED == tblRowOfSelectedDataObj) {
            dataObjStorage.add(dataObj);
        } else {
            dataObjStorage.set(tblRowOfSelectedDataObj, dataObj);
            dataObjIdsForUpdate.add(dataObj.getId());
        }
    }

    public void deleteObjects(List<AbstractVehicle> obsMarkedForDelete) {
        for (AbstractVehicle objForDel : obsMarkedForDelete) {
            long idObjForDel = objForDel.getId();
            if (idObjForDel > 0) {
                dataObjIdsForDelete.add(idObjForDel);
            }
        }
        dataObjStorage.removeAll(obsMarkedForDelete);
    }
}
