/*******************************************************************************
 * Copyright 2021-2026 Lenar Shamsutdinov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package home.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import home.Storage;
import home.db.dao.Dao;
import home.model.AbstractVehicle;
import home.model.Car;

abstract sealed class AbstractDmlTest extends AbstractDbTest permits PgDmlTest, SqliteDmlTest {

    private static final int ID_2 = 2;

    @BeforeEach
    @SkipIfDbUnavailable(dbTypes = { "PostgreSQL", "SQLite" })
    void clearDataTable() throws Exception {
        DbTestUtils.truncate(getTableName(), getDbType());
        Storage.INSTANCE.initDataObjs(Collections.emptyList());
    }

    @Test
    @SkipIfDbUnavailable(dbTypes = { "PostgreSQL", "SQLite" })
    void createDataObjTest() throws Exception {
        // There are no ids yet in new data objects. Ids will be set in database.
        // That is why we will set it later, from read database object.
        // It's need for correct comparison.
        List<AbstractVehicle> createdDataObjs = createThreeDataObjsWithoutId();
        saveDataObjsToDb(createdDataObjs);

        for (long i = 1; i <= createdDataObjs.size(); i++) {
            AbstractVehicle dataObjFromDb = readDataObjFromDbById(i);
            AbstractVehicle createdDataObj = createdDataObjs.get((int) (i - 1));

            // As said before, here we set id from read database object.
            createdDataObj.setId(dataObjFromDb.getId());

            assertEquals(createdDataObj, dataObjFromDb, """
                    Created object from DB does not match with expected.
                    object id: %d"
                    expected object: %s
                    actual object: %s
                    """.formatted(i, createdDataObj, dataObjFromDb));
        }
    }

    @Test
    @SkipIfDbUnavailable(dbTypes = { "PostgreSQL", "SQLite" })
    void updateDataObjTest() throws Exception {
        List<AbstractVehicle> createdDataObjs = createThreeDataObjsWithoutId();
        saveDataObjsToDb(createdDataObjs);

        AbstractVehicle readedDataObj2 = readDataObjFromDbById(ID_2);
        String newColor = "grey";
        readedDataObj2.setColor(newColor);
        saveEditedDataObjToDb(readedDataObj2, ID_2 - 1);

        AbstractVehicle updatedDataObj2 = readDataObjFromDbById(ID_2);

        assertEquals(readedDataObj2, updatedDataObj2, "Updated object from DB does not match with expected.");
    }

    @Test
    @SkipIfDbUnavailable(dbTypes = { "PostgreSQL", "SQLite" })
    void deleteDataObjTest() throws Exception {
        List<AbstractVehicle> createdDataObjs = createThreeDataObjsWithoutId();
        saveDataObjsToDb(createdDataObjs);

        AbstractVehicle readedDataObj2 = readDataObjFromDbById(ID_2);
        deleteDataObjsFromDb(List.of(readedDataObj2));

        // check the absence of dataObj with ID_2 in the DB
        try {
            readDataObjFromDbById(ID_2);
            fail("Expected java.lang.IndexOutOfBoundsException to be thrown, but nothing was thrown.");
        } catch (Exception e) {
            String expected = "Index -2 out of bounds for length 2";
            String actual = e.getMessage().strip();
            assertTrue(actual.endsWith(expected), """
                    Expected and actual error message does not match:
                    expected: %s
                    actual: %s
                    """.formatted(expected, actual));
        }

        assertEquals(2, DbTestUtils.count(getTableName()), "Expected and actual number of entries do not match.");
    }

    private List<AbstractVehicle> createThreeDataObjsWithoutId() {
        return List.of(createDataObjWithoutId("red", "k333or", true),
                createDataObjWithoutId("yellow", "m444ps", false),
                createDataObjWithoutId("green", "n555rt", true));
    }

    private AbstractVehicle createDataObjWithoutId(String color, String number,
            boolean isTransportsPassengers) {
        var car = new Car();
        car.setColor(color);
        car.setNumber(number);
        car.setDateTime(System.currentTimeMillis());
        car.setTransportsPassengers(isTransportsPassengers);
        return car;
    }

    private void saveDataObjsToDb(List<AbstractVehicle> dataObjs) throws Exception {
        DbTestUtils.runAndWait(() -> {
            // save created dataObj to internal application storage
            Storage.INSTANCE.initDataObjs(dataObjs);
            saveAndSyncData();
            return null;
        });
    }

    private AbstractVehicle readDataObjFromDbById(long id) throws Exception {
        return DbTestUtils.runAndWait(() -> {
            // read dataObj from DB by id
            AbstractVehicle dataObj = Dao.readOne(id);
            // synchronize internal application storage with DB
            Storage.INSTANCE.initDataObjs(Dao.readAll());
            return dataObj;
        });
    }

    @Deprecated(forRemoval = true) // because it gets more data from the database than it needs
    private AbstractVehicle readDataObjFromDbById(int id) throws Exception {
        return DbTestUtils.runAndWait(() -> {
            // read dataObj from DB by id
            List<AbstractVehicle> dataObjs = Dao.readAll();
            AbstractVehicle dataObj = getDataObjByIdViaBinarySearch(dataObjs, id);
            // synchronize internal application storage with DB
            Storage.INSTANCE.initDataObjs(Dao.readAll());
            return dataObj;
        });
    }

    private AbstractVehicle getDataObjByIdViaBinarySearch(List<AbstractVehicle> dataObjs, long id) {
        Comparator<AbstractVehicle> comparatorById = (v1, v2) -> Long.compare(v1.getId(), v2.getId());

        Collections.sort(dataObjs, comparatorById);

        var searchTemplate = new Car();
        searchTemplate.setId(id);

        int searchedObjIdx = Collections.binarySearch(dataObjs, searchTemplate, comparatorById);

        if (searchedObjIdx < 0) {
            throw new IndexOutOfBoundsException("Index %d out of bounds for length %d"
                    .formatted(searchedObjIdx, dataObjs.size()));
        }

        return dataObjs.get(searchedObjIdx);
    }

    private void saveEditedDataObjToDb(AbstractVehicle dataObj, int rowOfSelectedDataObj) throws Exception {
        DbTestUtils.runAndWait(() -> {
            // replace old dataObj by edited one in internal application storage
            Storage.INSTANCE.updateDataObj(dataObj, rowOfSelectedDataObj);
            saveAndSyncData();
            return null;
        });
    }

    private void deleteDataObjsFromDb(List<AbstractVehicle> dataObjs) throws Exception {
        DbTestUtils.runAndWait(() -> {
            // delete dataObjs from internal application storage
            Storage.INSTANCE.deleteDataObjs(dataObjs);
            saveAndSyncData();
            return null;
        });
    }

    private void saveAndSyncData() throws Exception {
        // save all changes of dataObj-s from internal application storage to DB
        Dao.saveAllChanges();
        // synchronize internal application storage with DB
        // (needed to sync IDs [DB -> internal_application_storage])
        Storage.INSTANCE.initDataObjs(Dao.readAll());
    }
}
