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

import home.IConsts;
import home.Storage;
import home.models.AbstractVehicle;
import home.models.Car;
import home.models.Motorcycle;
import home.models.Truck;
import home.models.VehicleType;
import home.utils.Utils;

abstract class AbstractDao implements IDao {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDao.class);

    private static final String SELECT_ALL = "SELECT * FROM vehicle;";

    private static final String SELECT_ONE = "SELECT * FROM vehicle WHERE id = ?;";

    private static final String INSERT = "INSERT INTO vehicle"
            + " ('type', 'color', 'number', 'date_time', 'is_transports_cargo',"
            + " 'is_transports_passengers', 'has_trailer', 'has_cradle')"
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String UPDATE = "UPDATE vehicle"
            + " SET type = ?, color = ?, number = ?, date_time = ?,"
            + " is_transports_cargo = ?, is_transports_passengers = ?,"
            + " has_trailer = ?, has_cradle = ?"
            + " WHERE id = ?;";

    private static final String DELETE = "DELETE FROM vehicle WHERE id in (%s);";

    protected AbstractDao() {
    }

    protected abstract Connection getConnection() throws SQLException;

    protected abstract int getTransactionIsolation();

    protected abstract Logger getLogger();

    @Override
    public AbstractVehicle readOne(long id) throws SQLException {
        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(getTransactionIsolation());

            try (var pstmt = conn.prepareStatement(SELECT_ONE)) {
                pstmt.setLong(1, id);

                var dataObjs = new ArrayList<AbstractVehicle>();
                try (var res = pstmt.executeQuery()) {
                    while (res.next()) {
                        dataObjs.add(convertResultToDataObj(res));
                    }
                    conn.commit();
                    conn.setAutoCommit(true);
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
    public ArrayList<AbstractVehicle> readAll() throws SQLException {
        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(getTransactionIsolation());
            try (var stmt = conn.createStatement();
                    var res = stmt.executeQuery(SELECT_ALL)) {
                var dataObjs = new ArrayList<AbstractVehicle>();
                while (res.next()) {
                    dataObjs.add(convertResultToDataObj(res));
                }
                conn.commit();
                conn.setAutoCommit(true);
                return dataObjs;
            }
        }
    }

    private AbstractVehicle convertResultToDataObj(ResultSet res) throws SQLException {
        String type = res.getString(DaoConsts.TYPE);
        VehicleType vehicleType = VehicleType.getVehicleType(type);
        if (vehicleType == null) {
            throw new SQLException("Wrong vehicle type received : " + type);
        }

        AbstractVehicle vehicle = null;
        switch (vehicleType) {
        case CAR:
            vehicle = new Car();
            var car = ((Car) vehicle);
            car.setTransportsPassengers(convertToBoolean(res.getInt(DaoConsts.IS_TRANSPORTS_PASSENGERS)));
            car.setHasTrailer(convertToBoolean(res.getInt(DaoConsts.HAS_TRAILER)));
            break;

        case TRUCK:
            vehicle = new Truck();
            var truck = ((Truck) vehicle);
            truck.setTransportsCargo(convertToBoolean(res.getInt(DaoConsts.IS_TRANSPORTS_CARGO)));
            truck.setHasTrailer(convertToBoolean(res.getInt(DaoConsts.HAS_TRAILER)));
            break;

        case MOTORCYCLE:
            vehicle = new Motorcycle();
            ((Motorcycle) vehicle).setHasCradle(convertToBoolean(res.getInt(DaoConsts.HAS_CRADLE)));
            break;
        }

        vehicle.setId(res.getLong(DaoConsts.ID));
        vehicle.setColor(res.getString(DaoConsts.COLOR));
        vehicle.setNumber(res.getString(DaoConsts.NUMBER));
        vehicle.setDateTime(res.getLong(DaoConsts.DATE_TIME));

        return vehicle;
    }

    private boolean convertToBoolean(int intBoolean) throws SQLException {
        switch (intBoolean) {
        case 0:
            return false;
        case 1:
            return true;
        default:
            throw new SQLException("Invalid value received for boolean variable: "
                    + intBoolean);
        }
    }

    @Override
    public void saveAllChanges() throws SQLException {
        var exceptions = new ArrayList<SQLException>();

        try {
            Long[] idsForDel = Storage.INSTANCE.getIdsForDelete();
            if (idsForDel.length > 0) {
                delete(idsForDel);
            }
        } catch (IllegalStateException e) {
            exceptions.add(new SQLException("DELETE operation error.", e));
        }

        try {
            Set<Long> idsForUpdate = Storage.INSTANCE.getIdsForUpdate();
            operation(this::update, dataObj -> dataObj.getId() > 0
                    && idsForUpdate.contains(dataObj.getId()));
        } catch (IllegalStateException e) {
            exceptions.add(new SQLException("UPDATE operation error.", e));
        }

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
        sqlOperationBatch(INSERT, dataObjs, "The information has not been added to the database: %s");
    }

    private void update(List<AbstractVehicle> dataObjs) {
        sqlOperationBatch(UPDATE, dataObjs, "The information in the database has not been updated: %s");
    }

    private void sqlOperationBatch(String sql, List<AbstractVehicle> dataObjs, String errorMsg) {
        boolean isUpdateOperation = UPDATE.equals(sql);

        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(getTransactionIsolation());
            try (var pstmt = conn.prepareStatement(sql)) {
                int operationsCount = 0;
                for (AbstractVehicle dataObj : dataObjs) {
                    pstmt.clearParameters();
                    fillStmtByDataFromObj(pstmt, dataObj, isUpdateOperation);
                    pstmt.addBatch();
                    operationsCount++;

                    // Execute every 1000 items.
                    if (operationsCount % 1000 == 0 || operationsCount == dataObjs.size()) {
                        checkBatchExecution(pstmt.executeBatch(),
                                String.format(errorMsg, dataObj), getLogger());
                        conn.commit();
                    }
                }
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                String error = String.format(errorMsg, IConsts.EMPTY_STRING);
                rollbackAndLog(conn, e, error);
                sqlOperationOneByOne(sql, dataObjs, error);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Sql INSERT/UPDATE operation error : ", e);
        }
    }

    private void checkBatchExecution(int[] batchResults, String errorMsg, Logger log)
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
                throw Utils.logAndCreateSqlException(msg.toString(), log);
            }

            msg.append("unknown result code '")
                    .append(batchResult).append("' was received.");
            throw Utils.logAndCreateSqlException(msg.toString(), log);
        }

        // TODO Perhaps the above logic should be replaced by this. Think about it.
//        if (batchResults != null && Arrays.stream(batchResults)
//                .anyMatch(batchResult -> batchResult < 1 && Statement.SUCCESS_NO_INFO != batchResult)) {
//            throw Utils.logAndCreateSqlException("Batch execution error: " + errorMsg, log);
//        }
    }

    private void rollbackAndLog(Connection conn, Exception e, String errorMsg) {
        LOG.error(errorMsg, e);
        try {
            conn.rollback();
        } catch (SQLException ex) {
            throw Utils.logAndCreateIllegalStateException(
                    errorMsg + " Sql rollback error.", LOG, e);
        }
    }

    private void sqlOperationOneByOne(String sql, List<AbstractVehicle> dataObjs, String errorMsg)
            throws SQLException {
        boolean isUpdateOperation = UPDATE.equals(sql);
        String operationType = isUpdateOperation ? "update" : "insert";

        Exception mainExeption = null;
        var errorsWithDataObjs = new ArrayList<String>();

        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(getTransactionIsolation());

            for (AbstractVehicle dataObj : dataObjs) {
                try (var pstmt = conn.prepareStatement(sql)) {
                    fillStmtByDataFromObj(pstmt, dataObj, isUpdateOperation);
                    pstmt.execute();
                    conn.commit();
                } catch (SQLException e) {
                    mainExeption = addException(mainExeption, e,
                            "Exception in %s mechanism one by one.".formatted(operationType));
                    errorsWithDataObjs.add(dataObj.toString() + "\n\t(" + e.getMessage() + ')');
                }
            }

            conn.setAutoCommit(true);
        }

        if (!errorsWithDataObjs.isEmpty()) {
            var sb = new StringBuilder();
            sb.append(errorMsg).append(" Can't ").append(operationType)
                    .append(":\n").append(String.join("\n", errorsWithDataObjs));

            throw Utils.logAndCreateSqlException(sb.toString(), LOG, mainExeption);
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
            pstmt.setInt(6, convertToInt(car.isTransportsPassengers()));
            pstmt.setInt(7, convertToInt(car.hasTrailer()));
            break;

        case TRUCK:
            Truck truck = (Truck) dataObj;
            pstmt.setInt(5, convertToInt(truck.isTransportsCargo()));
            pstmt.setInt(7, convertToInt(truck.hasTrailer()));
            break;

        case MOTORCYCLE:
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
        String sql = String.format(DELETE, idsStr);
        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(getTransactionIsolation());
            try (var pstmt = conn.prepareStatement(sql)) {
                if (pstmt.executeUpdate() <= 0) {
                    throw new SQLException("Information has not been deleted from the database."
                            + "\n Id list for delete: " + idsStr);
                }
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                rollbackAndLog(conn, e, "Error while removal several rows in one query.");
                deleteOneByOne(ids);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Sql DELETE operation error : ", e);
        }
    }

//    private void delete(Long[] ids) throws SQLException {
//        String placeHolders = "?,".repeat(ids.length);
//        String sql = String.format(DELETE, placeHolders.substring(0, placeHolders.length() - 1));
//        try (var conn = getConnection()) {
//            conn.setAutoCommit(false);
//            conn.setTransactionIsolation(getTransactionIsolation());
//            try (var pstmt = conn.prepareStatement(sql)) {
//                for (int i = 0; i < ids.length; i++) {
//                    pstmt.setLong(i + 1, ids[i]);
//                }
//                if (pstmt.executeUpdate() <= 0) {
//                    throw new SQLException("Information has not been deleted from the database."
//                            + "\n Id list for delete: " + Arrays.toString(ids));
//                }
//                conn.commit();
//                conn.setAutoCommit(true);
//            } catch (SQLException e) {
//                rollbackAndLog(conn, e, "Error while removal several rows in one query.");
//                deleteOneByOne(ids);
//            }
//        }
//    }

    private void deleteOneByOne(Long[] ids) throws SQLException {
        Exception mainExeption = null;
        var errorsWithIds = new ArrayList<String>();

        String sql = String.format(DELETE, "?");
        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(getTransactionIsolation());

            for (Long id : ids) {
                try (var pstmt = conn.prepareStatement(sql)) {
                    pstmt.setLong(1, id);
                    if (pstmt.executeUpdate() <= 0) {
                        errorsWithIds.add(id + IConsts.EMPTY_STRING);
                    }
                    conn.commit();
                } catch (SQLException e) {
                    mainExeption = addException(mainExeption, e,
                            "Exception in delete mechanism one by one.");
                    errorsWithIds.add(id + "\n\t(" + e.getMessage() + ')');
                }
            }

            conn.setAutoCommit(true);
        }

        if (!errorsWithIds.isEmpty()) {
            var sb = new StringBuilder();
            sb.append("Information has not been deleted from the database.")
                    .append("\nCan't delete objects with ids:\n")
                    .append(String.join("\n", errorsWithIds));

            throw Utils.logAndCreateSqlException(sb.toString(), LOG, mainExeption);
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
