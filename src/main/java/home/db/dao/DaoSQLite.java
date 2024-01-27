
package home.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import home.db.Connector;
import home.db.DbConsts;
import home.models.AbstractVehicle;
import home.models.Car;
import home.models.Motorcycle;
import home.models.Truck;
import home.models.VehicleType;

public class DaoSQLite implements Dao {

    private static final String SELECT_ALL = "SELECT * FROM vehicle;";
    private static final String SELECT_ONE = "SELECT * FROM vehicle WHERE id=%d;";

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

    private static Dao instance;

    private DaoSQLite() {
    }

    public static Dao getInstance() {
        if (instance == null) {
            instance = new DaoSQLite();
        }
        return instance;
    }

    @Override
    public AbstractVehicle readOne(long id) throws SQLException {
        try (var stmt = Connector.getConnection().createStatement();
                var res = stmt.executeQuery(String.format(SELECT_ONE, id));) {
            AbstractVehicle dataObjs = null;
            while (res.next()) {
                dataObjs = convertResultToDataObj(res);
            }
            return dataObjs;
        } finally {
            Connector.closeConnection();
        }
    }

    @Override
    public ArrayList<AbstractVehicle> readAll() throws SQLException {
        try (var stmt = Connector.getConnection().createStatement();
                var res = stmt.executeQuery(SELECT_ALL);) {
            var dataObjs = new ArrayList<AbstractVehicle>();
            while (res.next()) {
                dataObjs.add(convertResultToDataObj(res));
            }
            return dataObjs;
        } finally {
            Connector.closeConnection();
        }
    }

    private AbstractVehicle convertResultToDataObj(ResultSet res) throws SQLException {
        String type = res.getString(DbConsts.TYPE);
        VehicleType vehicleType = VehicleType.getVehicleType(type);
        if (vehicleType == null) {
            throw new SQLException("Wrong vehicle type received : " + type);
        }

        AbstractVehicle vehicle = null;
        switch (vehicleType) {
        case CAR:
            vehicle = new Car();
            var car = ((Car) vehicle);
            car.setTransportsPassengers(
                    convertToBoolean(res.getInt(DbConsts.IS_TRANSPORTS_PASSENGERS)));
            car.setHasTrailer(convertToBoolean(res.getInt(DbConsts.HAS_TRAILER)));
            break;

        case TRUCK:
            vehicle = new Truck();
            var truck = ((Truck) vehicle);
            truck.setTransportsCargo(
                    convertToBoolean(res.getInt(DbConsts.IS_TRANSPORTS_CARGO)));
            truck.setHasTrailer(convertToBoolean(res.getInt(DbConsts.HAS_TRAILER)));
            break;

        case MOTORCYCLE:
            vehicle = new Motorcycle();
            ((Motorcycle) vehicle).setHasCradle(
                    convertToBoolean(res.getInt(DbConsts.HAS_CRADLE)));
            break;
        }

        vehicle.setId(res.getLong(DbConsts.ID));
        vehicle.setColor(res.getString(DbConsts.COLOR));
        vehicle.setNumber(res.getString(DbConsts.NUMBER));
        vehicle.setDateTime(res.getLong(DbConsts.DATE_TIME));

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
    public void create(AbstractVehicle dataObj) throws SQLException {
        try (var stmt = Connector.getConnection().prepareStatement(INSERT)) {
            VehicleType dataObjType = dataObj.getType();

            stmt.setString(1, dataObjType.getType());
            stmt.setString(2, dataObj.getColor());
            stmt.setString(3, dataObj.getNumber());
            stmt.setLong(4, dataObj.getDateTime());

            switch (dataObjType) {
            case CAR:
                Car car = (Car) dataObj;
                stmt.setInt(6, convertToInt(car.isTransportsPassengers()));
                stmt.setInt(7, convertToInt(car.hasTrailer()));
                break;

            case TRUCK:
                Truck truck = (Truck) dataObj;
                stmt.setInt(5, convertToInt(truck.isTransportsCargo()));
                stmt.setInt(7, convertToInt(truck.hasTrailer()));
                break;

            case MOTORCYCLE:
                stmt.setInt(8, convertToInt(((Motorcycle) dataObj).hasCradle()));
                break;
            }

            if (stmt.executeUpdate() <= 0) {
                throw new SQLException("The information has not been added to the database.");
            }
        } finally {
            Connector.closeConnection();
        }
    }

    @Override
    public void update(AbstractVehicle dataObj) throws SQLException {
        try (var stmt = Connector.getConnection().prepareStatement(UPDATE)) {
            VehicleType dataObjType = dataObj.getType();

            stmt.setString(1, dataObjType.getType());
            stmt.setString(2, dataObj.getColor());
            stmt.setString(3, dataObj.getNumber());
            stmt.setLong(4, dataObj.getDateTime());

            switch (dataObjType) {
            case CAR:
                Car car = (Car) dataObj;
                stmt.setInt(6, convertToInt(car.isTransportsPassengers()));
                stmt.setInt(7, convertToInt(car.hasTrailer()));
                break;

            case TRUCK:
                Truck truck = (Truck) dataObj;
                stmt.setInt(5, convertToInt(truck.isTransportsCargo()));
                stmt.setInt(7, convertToInt(truck.hasTrailer()));
                break;

            case MOTORCYCLE:
                stmt.setInt(8, convertToInt(((Motorcycle) dataObj).hasCradle()));
                break;
            }

            stmt.setLong(9, dataObj.getId());
            if (stmt.executeUpdate() <= 0) {
                throw new SQLException("The information in the database has not been updated.");
            }
        } finally {
            Connector.closeConnection();
        }
    }

    private int convertToInt(boolean booleaVal) {
        return booleaVal ? 1 : 0;
    }

    @Override
    public void delete(Long[] ids) throws SQLException {
        String placeHolders = "?,".repeat(ids.length);
        String sql = String.format(DELETE, placeHolders.substring(0, placeHolders.length() - 1));
        try (var stmt = Connector.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < ids.length; i++) {
                stmt.setLong(i + 1, ids[i]);
            }
            if (stmt.executeUpdate() <= 0) {
                throw new SQLException("Information has not been deleted from the database."
                        + "\n Id list for delete: " + ids);
            }
        } finally {
            Connector.closeConnection();
        }
    }
}
