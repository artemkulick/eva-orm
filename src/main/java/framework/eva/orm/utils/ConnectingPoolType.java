package framework.eva.orm.utils;

import framework.eva.orm.communication.ConnectingPoolDBCP;
import framework.eva.orm.communication.ConnectingPoolTomcat;
import framework.eva.orm.communication.ConnectingPool;
import framework.eva.orm.configuration.ServerProperties;

/**
 * @author artem
 * @since 29.10.17
 */
public enum ConnectingPoolType {
    DBCP {
        @Override
        public ConnectingPool newInstance(ServerProperties prop) {
            return new ConnectingPoolDBCP(prop);
        }
    },
    TOMCAT {
        @Override
        public ConnectingPool newInstance(ServerProperties prop) {
            return new ConnectingPoolTomcat(prop);
        }
    };

    public abstract ConnectingPool newInstance(ServerProperties prop);
}