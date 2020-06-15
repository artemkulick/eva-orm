package framework.eva.orm.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author artem
 * @since 29.10.17
 */
public interface ConnectingPool {
    String PROP_DRIVERCLASSNAME = "driverClassName";
    String PROP_URL = "url";
    String PROP_USERNAME = "username";
    String PROP_PASSWORD = "password";

    String PROP_DEFAULTAUTOCOMMIT = "defaultAutoCommit";
    String PROP_DEFAULTREADONLY = "defaultReadOnly";

    String PROP_INITIALSIZE = "initialSize";
    String PROP_MAXACTIVE = "maxActive";
    String PROP_MAXIDLE = "maxIdle";
    String PROP_MINIDLE = "minIdle";
    String PROP_MAXWAIT = "maxWait";
    String PROP_MAXAGE = "maxAge";

    String PROP_TESTONBORROW = "testOnBorrow";
    String PROP_TESTONRETURN = "testOnReturn";
    String PROP_TESTWHILEIDLE = "testWhileIdle";
    String PROP_VALIDATIONQUERY = "validationQuery";
    String PROP_VALIDATIONINTERVAL = "validationInterval";

    String PROP_MINEVICTABLEIDLETIMEMILLIS = "minEvictableIdleTimeMillis";
    String PROP_TIMEBETWEENEVICTIONRUNSMILLIS = "timeBetweenEvictionRunsMillis";

    String PROP_REMOVEABANDONED = "removeAbandoned";
    String PROP_REMOVEABANDONEDTIMEOUT = "removeAbandonedTimeout";
    String PROP_LOGABANDONED = "logAbandoned";

    String PROP_JMX_ENABLED = "jmxEnabled";

    Logger logger = LoggerFactory.getLogger(ConnectingPool.class);

    Connection getConnection() throws SQLException;

    /**
     *
     * @param level TransactionIsolationLevel
     */
    Connection getConnection(int level) throws SQLException;

    void shutdown();

    String getStats();
}