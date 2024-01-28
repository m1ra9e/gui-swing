package home.db.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.db.Connector;

public final class DaoSQLite extends AbstractDao {

    private static final Logger LOG = LoggerFactory.getLogger(DaoSQLite.class);

    private static IDao instance;

    private DaoSQLite() {
    }

    public static IDao getInstance() {
        if (instance == null) {
            instance = new DaoSQLite();
        }
        return instance;
    }

    @Override
    protected Connection getConnection() throws SQLException {
        return Connector.getConnectionToSQLite();
    }

    @Override
    protected int getTransactionIsolation() {
        return Connection.TRANSACTION_SERIALIZABLE;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
