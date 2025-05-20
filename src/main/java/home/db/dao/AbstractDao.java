/*******************************************************************************
 * Copyright 2021-2025 Lenar Shamsutdinov
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
package home.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.Const;
import home.Storage;
import home.db.conn.Connector;
import home.model.AbstractVehicle;
import home.model.Car;
import home.model.Motorcycle;
import home.model.Truck;
import home.model.VehicleType;
import home.utils.LogUtils;
import home.utils.NamedFormatter;

abstract sealed class AbstractDao implements IDao permits PgDao, SQLiteDao {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDao.class);

    private static final String SELECT_ALL = NamedFormatter
            .format("SELECT * FROM ${table_name}",
                    DaoConst.PLACEHOLDER_VALUES);

    private static final String SELECT_ONE = NamedFormatter
            .format("SELECT * FROM ${table_name} WHERE ${col_id} = ?",
                    DaoConst.PLACEHOLDER_VALUES);

    // !!! the order of the columns in the INSERT query must be the same as in the
    // UPDATE query because they both use the fillStmtByDataFromObj method.
    private static final String INSERT = NamedFormatter.format("""
            INSERT INTO ${table_name}
            (${col_type}, ${col_color}, ${col_number}, ${col_date_time},
            ${col_is_transports_cargo}, ${col_is_transports_passengers},
            ${col_has_trailer}, ${col_has_cradle})
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """, DaoConst.PLACEHOLDER_VALUES);

    // !!! the order of the columns in the UPDATE query must be the same as in the
    // INSERT query because they both use the fillStmtByDataFromObj method.
    private static final String UPDATE = NamedFormatter.format("""
            UPDATE ${table_name} SET
            ${col_type} = ?, ${col_color} = ?, ${col_number} = ?, ${col_date_time} = ?,
            ${col_is_transports_cargo} = ?, ${col_is_transports_passengers} = ?,
            ${col_has_trailer} = ?, ${col_has_cradle} = ?
            WHERE ${col_id} = ?
            """, DaoConst.PLACEHOLDER_VALUES);

    private static final String DELETE = NamedFormatter
            .format("DELETE FROM ${table_name} WHERE ${col_id} ",
                    DaoConst.PLACEHOLDER_VALUES)
            + "IN (%s)";

    private static final int BATCH_SIZE = 1_000;

    // https://www.ibm.com/docs/en/db2woc?topic=messages-sqlstate
    // 08 - Connection Exception
    private static final String CONNECTION_ERROR_CODE = "08";

    private static final int FALSE_VALUE_FOR_DB = 0;

    protected abstract int getTransactionIsolation();

    @Override
    public AbstractVehicle readOne(long id) throws SQLException {
        try (var conn = Connector.getConnection()) {
            conn.setTransactionIsolation(getTransactionIsolation());

            try (var pstmt = conn.prepareStatement(SELECT_ONE)) {
                pstmt.setLong(1, id);

                var dataObjs = new ArrayList<AbstractVehicle>();
                try (var res = pstmt.executeQuery()) {
                    while (res.next()) {
                        dataObjs.add(convertResultToDataObj(res));
                    }
                }

                if (dataObjs.size() > 1) {
                    throw new SQLException(
                            "There are several values for id=" + id + " in database.");
                }

                return dataObjs.get(0);
            }
        }
    }

    @Override
    public List<AbstractVehicle> readAll() throws SQLException {
        try (var conn = Connector.getConnection()) {
            conn.setTransactionIsolation(getTransactionIsolation());
            try (var stmt = conn.createStatement();
                    var res = stmt.executeQuery(SELECT_ALL)) {
                var dataObjs = new ArrayList<AbstractVehicle>();
                while (res.next()) {
                    dataObjs.add(convertResultToDataObj(res));
                }
                return dataObjs;
            }
        }
    }

    private AbstractVehicle convertResultToDataObj(ResultSet res) throws SQLException {
        String type = res.getString(DaoConst.TYPE);

        VehicleType vehicleType = getVehicleTypeOrThrowSqlException(type);

        AbstractVehicle vehicle = switch (vehicleType) {
            case CAR -> {
                var car = new Car();
                car.setTransportsPassengers(convertToBoolean(res.getInt(DaoConst.IS_TRANSPORTS_PASSENGERS)));
                car.setHasTrailer(convertToBoolean(res.getInt(DaoConst.HAS_TRAILER)));
                yield car;
            }
            case TRUCK -> {
                var truck = new Truck();
                truck.setTransportsCargo(convertToBoolean(res.getInt(DaoConst.IS_TRANSPORTS_CARGO)));
                truck.setHasTrailer(convertToBoolean(res.getInt(DaoConst.HAS_TRAILER)));
                yield truck;
            }
            case MOTORCYCLE -> {
                var motorcycle = new Motorcycle();
                motorcycle.setHasCradle(convertToBoolean(res.getInt(DaoConst.HAS_CRADLE)));
                yield motorcycle;
            }
        };

        vehicle.setId(res.getLong(DaoConst.ID));
        vehicle.setColor(res.getString(DaoConst.COLOR));
        vehicle.setNumber(res.getString(DaoConst.NUMBER));
        vehicle.setDateTime(res.getLong(DaoConst.DATE_TIME));

        return vehicle;
    }

    private VehicleType getVehicleTypeOrThrowSqlException(String type) throws SQLException {
        try {
            return VehicleType.getVehicleType(type);
        } catch (IllegalArgumentException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    private boolean convertToBoolean(int intBoolean) throws SQLException {
        return switch (intBoolean) {
            case 0 -> false;
            case 1 -> true;
            default -> throw new SQLException("Invalid value received for boolean variable: "
                    + intBoolean);
        };
    }

    @Override
    public void saveAllChanges() throws SQLException {
        var exceptions = new ArrayList<SQLException>();

        // delete operations
        try {
            Long[] idsForDel = Storage.INSTANCE.getIdsForDelete();
            if (idsForDel.length > 0) {
                delete(idsForDel);
            }
        } catch (IllegalStateException e) {
            exceptions.add(new SQLException("DELETE operation error.", e));
        }

        // update operations
        try {
            Set<Long> idsForUpdate = Storage.INSTANCE.getIdsForUpdate();
            operation(this::update, dataObj -> dataObj.getId() > 0
                    && idsForUpdate.contains(dataObj.getId()));
        } catch (IllegalStateException e) {
            exceptions.add(new SQLException("UPDATE operation error.", e));
        }

        // insert operations
        try {
            operation(this::insert, dataObj -> dataObj.getId() == 0);
        } catch (IllegalStateException e) {
            exceptions.add(new SQLException("INSERT operation error.", e));
        }

        if (!exceptions.isEmpty()) {
            var mainException = new SQLException("Save all changes operation error.");
            exceptions.forEach(mainException::addSuppressed);
            throw mainException;
        }
    }

    private void operation(Consumer<List<AbstractVehicle>> sqlOperation,
            Predicate<AbstractVehicle> objFilter) {
        List<AbstractVehicle> objs = Storage.INSTANCE.getAll().stream()
                .filter(objFilter).collect(Collectors.toList());
        if (!objs.isEmpty()) {
            sqlOperation.accept(objs);
        }
    }

    @Override
    public void saveAs() throws SQLException {
        try {
            insert(Storage.INSTANCE.getAll());
        } catch (IllegalStateException e) {
            throw new SQLException("Save as operation error (insert). ", e);
        }
    }

    private void insert(List<AbstractVehicle> dataObjs) {
        sqlOperationBatch(false, dataObjs, "The information has not been added to the database: %s");
    }

    private void update(List<AbstractVehicle> dataObjs) {
        sqlOperationBatch(true, dataObjs, "The information in the database has not been updated: %s");
    }

    private void sqlOperationBatch(boolean isUpdateOperation,
            List<AbstractVehicle> dataObjs, String errorMsg) {
        String sql = isUpdateOperation ? UPDATE : INSERT;

        try (var conn = Connector.getConnection()) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(getTransactionIsolation());
            try (var pstmt = conn.prepareStatement(sql)) {
                int operationsCount = 0;
                for (AbstractVehicle dataObj : dataObjs) {
                    pstmt.clearParameters();
                    fillStmtByDataFromObj(pstmt, dataObj, isUpdateOperation);
                    pstmt.addBatch();
                    operationsCount++;

                    // Execute every BATCH_SIZE items.
                    if (operationsCount % BATCH_SIZE == 0 || operationsCount == dataObjs.size()) {
                        checkBatchExecution(pstmt.executeBatch(),
                                errorMsg.formatted(dataObj), LOG);
                        conn.commit();
                    }
                }
            } catch (SQLException e) {
                String error = errorMsg.formatted(Const.EMPTY_STRING);

                checkConnectionState(e, error);

                rollbackAndLog(conn, e, error);
                sqlOperationOneByOne(conn, sql, dataObjs, isUpdateOperation, error);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            String operationType = isUpdateOperation ? "UPDATE" : "INSERT";
            throw new IllegalStateException("Sql " + operationType + " operation error : ", e);
        }
    }

    void checkBatchExecution(int[] batchResults, String errorMsg, Logger log)
            throws SQLException {
        if (batchResults == null) {
            log.warn("Batch execution result is null.\n Check! \n Maybe " + errorMsg);
            return;
        }

        for (int batchResult : batchResults) {
            if (batchResult >= 0 || Statement.SUCCESS_NO_INFO == batchResult) {
                // Everything is fine.
                continue;
            }

            var msg = new StringBuilder();
            msg.append("Batch execution error:\n").append(errorMsg)
                    .append("\n").append("When executing the batch, ");

            if (Statement.EXECUTE_FAILED == batchResult) {
                msg.append("result code 'EXECUTE_FAILED' was received.");
                throw LogUtils.logAndCreateSqlException(msg.toString(), log);
            }

            msg.append("unknown result code '")
                    .append(batchResult).append("' was received.");
            throw LogUtils.logAndCreateSqlException(msg.toString(), log);
        }
    }

    private void rollbackAndLog(Connection conn, Exception e, String errorMsg) {
        LOG.error(errorMsg, e);
        try {
            conn.rollback();
        } catch (SQLException ex) {
            throw LogUtils.logAndCreateIllegalStateException(
                    errorMsg + " Sql rollback error.", LOG, e);
        }
    }

    private void checkConnectionState(SQLException e, String errorMsg) throws SQLException {
        String sqlState = e.getSQLState();
        if (sqlState.startsWith(CONNECTION_ERROR_CODE)) {
            throw LogUtils.logAndCreateSqlException(
                    "%s:\nConnection error (code %s)".formatted(errorMsg, sqlState), LOG, e);
        }
    }

    private void sqlOperationOneByOne(Connection conn, String sql,
            List<AbstractVehicle> dataObjs, boolean isUpdateOperation, String errorMsg)
            throws SQLException {
        String operationType = isUpdateOperation ? "update" : "insert";

        Exception mainExeption = null;
        var errorsWithDataObjs = new ArrayList<String>();

        conn.setAutoCommit(true);
        for (AbstractVehicle dataObj : dataObjs) {
            try (var pstmt = conn.prepareStatement(sql)) {
                fillStmtByDataFromObj(pstmt, dataObj, isUpdateOperation);
                pstmt.execute();
            } catch (SQLException e) {
                mainExeption = addException(mainExeption, e,
                        "Exception in %s mechanism one by one.".formatted(operationType));
                errorsWithDataObjs.add(dataObj.toString() + "\n\t(" + e.getMessage() + ')');
            }
        }

        if (!errorsWithDataObjs.isEmpty()) {
            var sb = new StringBuilder();
            sb.append(errorMsg).append(" Can't ").append(operationType)
                    .append(":\n").append(String.join("\n", errorsWithDataObjs));

            throw LogUtils.logAndCreateSqlException(sb.toString(), LOG, mainExeption);
        }
    }

    private void fillStmtByDataFromObj(PreparedStatement pstmt, AbstractVehicle dataObj,
            boolean isUpdateOperation) throws SQLException {
        VehicleType dataObjType = dataObj.getType();

        pstmt.setString(1, dataObjType.getType());
        pstmt.setString(2, dataObj.getColor());
        pstmt.setString(3, dataObj.getNumber());
        pstmt.setLong(4, dataObj.getDateTime());

        switch (dataObjType) {
            case CAR:
                Car car = (Car) dataObj;
                pstmt.setInt(5, FALSE_VALUE_FOR_DB);
                pstmt.setInt(6, convertToInt(car.isTransportsPassengers()));
                pstmt.setInt(7, convertToInt(car.hasTrailer()));
                pstmt.setInt(8, FALSE_VALUE_FOR_DB);
                break;

            case TRUCK:
                Truck truck = (Truck) dataObj;
                pstmt.setInt(5, convertToInt(truck.isTransportsCargo()));
                pstmt.setInt(6, FALSE_VALUE_FOR_DB);
                pstmt.setInt(7, convertToInt(truck.hasTrailer()));
                pstmt.setInt(8, FALSE_VALUE_FOR_DB);
                break;

            case MOTORCYCLE:
                pstmt.setInt(5, FALSE_VALUE_FOR_DB);
                pstmt.setInt(6, FALSE_VALUE_FOR_DB);
                pstmt.setInt(7, FALSE_VALUE_FOR_DB);
                pstmt.setInt(8, convertToInt(((Motorcycle) dataObj).hasCradle()));
                break;
        }

        if (isUpdateOperation) {
            pstmt.setLong(9, dataObj.getId());
        }
    }

    private int convertToInt(boolean booleaVal) {
        return booleaVal ? 1 : 0;
    }

    private void delete(Long[] ids) {
        String idsStr = Stream.of(ids).map(String::valueOf).collect(Collectors.joining(","));
        String sql = DELETE.formatted(idsStr);
        try (var conn = Connector.getConnection()) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(getTransactionIsolation());
            try (var pstmt = conn.prepareStatement(sql)) {
                if (pstmt.executeUpdate() <= 0) {
                    throw new SQLException("Information has not been deleted from the database."
                            + "\n Id list for delete: " + idsStr);
                }
                conn.commit();
            } catch (SQLException e) {
                String errorMsg = "Error while removal several rows in one query";

                checkConnectionState(e, errorMsg);

                rollbackAndLog(conn, e, errorMsg);
                deleteOneByOne(conn, ids);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Sql DELETE operation error : ", e);
        }
    }

    private void deleteOneByOne(Connection conn, Long[] ids) throws SQLException {
        Exception mainExeption = null;
        var errorsWithIds = new ArrayList<String>();

        conn.setAutoCommit(true);
        String sql = DELETE.formatted("?");

        for (Long id : ids) {
            try (var pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, id);
                if (pstmt.executeUpdate() <= 0) {
                    errorsWithIds.add(id + Const.EMPTY_STRING);
                }
            } catch (SQLException e) {
                mainExeption = addException(mainExeption, e,
                        "Exception in delete mechanism one by one.");
                errorsWithIds.add(id + "\n\t(" + e.getMessage() + ')');
            }
        }

        if (!errorsWithIds.isEmpty()) {
            var sb = new StringBuilder();
            sb.append("Information has not been deleted from the database.")
                    .append("\nCan't delete objects with ids:\n")
                    .append(String.join("\n", errorsWithIds));

            throw LogUtils.logAndCreateSqlException(sb.toString(), LOG, mainExeption);
        }
    }

    private Exception addException(Exception mainExeption, Exception newException, String msg) {
        if (mainExeption == null) {
            mainExeption = new SQLException(msg);
        }
        mainExeption.addSuppressed(newException);
        return mainExeption;
    }
}
