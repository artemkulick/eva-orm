package framework.eva.orm.communication;


import framework.eva.orm.configuration.ServerProperties;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author artem
 * @since 29.10.17
 */
public class ConnectingPoolDBCP implements ConnectingPool {
    private final org.apache.commons.dbcp.BasicDataSource dataSource;

    public ConnectingPoolDBCP(ServerProperties config) {
        logger.info("ConnectingPoolDBCP: connection pool initializing...");
        dataSource = new org.apache.commons.dbcp.BasicDataSource();
        try {
            Class.forName(config.getProperty(PROP_DRIVERCLASSNAME)).newInstance();

            dataSource.setUrl(config.getProperty(PROP_URL));
            dataSource.setDriverClassName(config.getProperty(PROP_DRIVERCLASSNAME));
            dataSource.setUsername(config.getProperty(PROP_USERNAME));
            dataSource.setPassword(config.getProperty(PROP_PASSWORD));
            //
            dataSource.setTestWhileIdle(config.getBoolean(PROP_TESTWHILEIDLE, true));
            dataSource.setTestOnBorrow(config.getBoolean(PROP_TESTONBORROW, false));
            dataSource.setTestOnReturn(config.getBoolean(PROP_TESTONRETURN, false));
            dataSource.setDefaultReadOnly(config.getBoolean(PROP_DEFAULTREADONLY, false));
            dataSource.setDefaultAutoCommit(config.getBoolean(PROP_DEFAULTAUTOCOMMIT, true));
            dataSource.setRemoveAbandonedTimeout(config.getInteger(PROP_VALIDATIONINTERVAL, 60));
            dataSource.setLogAbandoned(config.getBoolean(PROP_LOGABANDONED, false));
            dataSource.setRemoveAbandoned(config.getBoolean(PROP_REMOVEABANDONED, true));
            //
            dataSource.setValidationQuery(config.getProperty(PROP_VALIDATIONQUERY));
            dataSource.setValidationQueryTimeout(config.getInteger(PROP_VALIDATIONINTERVAL, 30000));
            //
            dataSource.setMinEvictableIdleTimeMillis(config.getInteger(PROP_MINEVICTABLEIDLETIMEMILLIS, 600000));
            dataSource.setTimeBetweenEvictionRunsMillis(config.getInteger(PROP_TIMEBETWEENEVICTIONRUNSMILLIS, 60000));
            //
            dataSource.setInitialSize(config.getInteger(PROP_INITIALSIZE, 5));
            dataSource.setMaxActive(config.getInteger(PROP_MAXACTIVE, 100));
            dataSource.setMaxIdle(config.getInteger(PROP_MAXIDLE, 100));
            dataSource.setMinIdle(config.getInteger(PROP_MINIDLE, 5));
            dataSource.setMaxWait(config.getInteger(PROP_MAXWAIT, 10000));

            /* Test the connection */
            dataSource.getConnection().close();
        } catch (Exception e) {
            logger.error("ConnectingPoolDBCP initialization failed: " + e.getMessage() + "!", e);
        } finally {
            logger.info("ConnectingPoolDBCP: connection pool initialized.");
        }
    }

    @Override
    public void shutdown() {
        try {
            dataSource.close();
        } catch (SQLException e) {
            logger.error("", e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public Connection getConnection(int level) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setTransactionIsolation(level);
        return connection;
    }

    @Override
    public String getStats() {
        String line = "";
        line += "======== DBCP Stats ==========\n";
        line += "InitialSize............ " + getInitialSize() + "\n";
        line += "BusyConnectionCount.... " + getBusyConnectionCount() + "\n";
        line += "IdleConnectionCount....." + getIdleConnectionCount() + "\n";
        line += "NumActive.............. " + getNumActive() + "\n";
        line += "MaxActive.............. " + getMaxActive() + "\n";
        return line;
    }

    private int getBusyConnectionCount() {
        return dataSource.getNumActive();
    }

    private int getInitialSize() {
        return dataSource.getInitialSize();
    }

    private int getMaxActive() {
        return dataSource.getMaxActive();
    }

    private int getIdleConnectionCount() {
        return dataSource.getNumIdle();
    }

    private int getNumActive() {
        return dataSource.getNumActive();
    }
}
