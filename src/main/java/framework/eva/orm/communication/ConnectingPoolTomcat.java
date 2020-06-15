package framework.eva.orm.communication;

import framework.eva.orm.configuration.ServerProperties;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * @author artem
 * @since 29.10.17
 */

public final class ConnectingPoolTomcat implements ConnectingPool
{
    private org.apache.tomcat.jdbc.pool.DataSource dataSource;

    public ConnectingPoolTomcat(ServerProperties prop)
    {
        try
        {
            Class.forName(prop.getProperty(PROP_DRIVERCLASSNAME)).newInstance();

            PoolProperties p = new PoolProperties();
            p.setUrl(prop.getProperty(PROP_URL));
            p.setDriverClassName(prop.getProperty(PROP_DRIVERCLASSNAME));
            p.setUsername(prop.getProperty(PROP_USERNAME));
            p.setPassword(prop.getProperty(PROP_PASSWORD));
            //
            p.setJmxEnabled(prop.getBoolean(PROP_JMX_ENABLED, false));
            p.setTestWhileIdle(prop.getBoolean(PROP_TESTWHILEIDLE, true));
            p.setTestOnBorrow(prop.getBoolean(PROP_TESTONBORROW, false));
            p.setTestOnReturn(prop.getBoolean(PROP_TESTONRETURN, false));
            p.setTestOnConnect(false);
            //
            // p.setName(poolName);
            p.setDefaultReadOnly(prop.getBoolean(PROP_DEFAULTREADONLY, false));
            p.setDefaultAutoCommit(prop.getBoolean(PROP_DEFAULTAUTOCOMMIT, true));
            p.setRemoveAbandonedTimeout(prop.getInteger(PROP_VALIDATIONINTERVAL, 60));
            p.setLogAbandoned(prop.getBoolean(PROP_LOGABANDONED, false));
            p.setRemoveAbandoned(prop.getBoolean(PROP_REMOVEABANDONED, true));
            //
            p.setValidationQuery(prop.getProperty(PROP_VALIDATIONQUERY));
            p.setValidationInterval(prop.getInteger(PROP_VALIDATIONINTERVAL, 30000));
            //
            p.setMinEvictableIdleTimeMillis(prop.getInteger(PROP_MINEVICTABLEIDLETIMEMILLIS, 600000));
            p.setTimeBetweenEvictionRunsMillis(prop.getInteger(PROP_TIMEBETWEENEVICTIONRUNSMILLIS, 60000));
            //
            p.setInitialSize(prop.getInteger(PROP_INITIALSIZE, 5));
            p.setMaxActive(prop.getInteger(PROP_MAXACTIVE, 100));
            p.setMaxIdle(prop.getInteger(PROP_MAXIDLE, 100));
            p.setMinIdle(prop.getInteger(PROP_MINIDLE, 5));
            p.setMaxWait(prop.getInteger(PROP_MAXWAIT, 10000));
            // p.setMaxAge(prop.getInteger(PROP_MAXAGE, 0));

            Jdbc4DriverValidator validator = new Jdbc4DriverValidator(5);

            dataSource = new DataSource(p);
            dataSource.setValidator(validator);
        }
        catch(Exception e)
        {
            logger.error("ConnectingPoolTomcat initialization failed: " + e.getMessage() + "!", e);
        }
        finally
        {
            logger.info("ConnectingPoolTomcat: initialized connection pool.");
        }

    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return dataSource.getConnection();
    }

    @Override
    public Connection getConnection(int level) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setTransactionIsolation(level);
        return connection;
    }

    @Override
    public void shutdown()
    {
        dataSource.close();
    }

    @Override
    public String getStats()
    {
        String line = "";
        line += "======== Tomcat Stats ==========\n";
        line += "InitialSize............ " + dataSource.getInitialSize() + "\n";
        line += "NumActive.............. " + dataSource.getNumActive() + "\n";
        line += "NumIdle................ " + dataSource.getNumIdle() + "\n";
        line += "WaitCount.............. " + dataSource.getWaitCount() + "\n";
        line += "Idle................... " + dataSource.getIdle() + "\n";
        line += "Active................. " + dataSource.getActive() + "\n";
        return line;
    }

    private static final class Jdbc4DriverValidator implements Validator
    {
        private static final Logger logger = LoggerFactory.getLogger(Jdbc4DriverValidator.class);

        private final int validationTimeout;

        Jdbc4DriverValidator(final int timeOut)
        {
            validationTimeout = timeOut;
        }

        /**
         * Validate a connection and return a boolean to indicate if it's valid.
         *
         * @param dbConn         the Connection object to test
         * @param validateAction the action used. One of {@link org.apache.tomcat.jdbc.pool.PooledConnection#VALIDATE_BORROW}, {@link org.apache.tomcat.jdbc.pool.PooledConnection#VALIDATE_IDLE}, {@link org.apache.tomcat.jdbc.pool.PooledConnection#VALIDATE_INIT}
         *                       or {@link org.apache.tomcat.jdbc.pool.PooledConnection#VALIDATE_RETURN}
         * @return true if the connection is valid
         */
        @Override
        public boolean validate(Connection dbConn, int validateAction)
        {
            boolean valid = false;
            try
            {
                valid = dbConn != null && dbConn.isValid(validationTimeout);
            }
            catch (SQLException e)
            {
                logger.error("Jdbc4DriverValidator: error invoking the JDBC Connection.isValid()", e);
            }
            return valid;
        }

    }
}