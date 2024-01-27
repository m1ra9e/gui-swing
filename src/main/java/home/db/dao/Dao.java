package home.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import home.models.AbstractVehicle;

public interface Dao {

    AbstractVehicle readOne(long id) throws SQLException;

    ArrayList<AbstractVehicle> readAll() throws SQLException;

    void create(AbstractVehicle dataObj) throws SQLException;

    void update(AbstractVehicle dataObj) throws SQLException;

    void delete(Long[] ids) throws SQLException;
}
