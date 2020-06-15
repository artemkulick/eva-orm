package framework.eva.orm.communication;

import framework.eva.orm.concurrent.CountedThreadFactory;
import framework.eva.orm.configuration.ServerProperties;
import framework.eva.orm.utils.ConnectingPoolType;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author artem
 * @since 29.10.17
 */
public class DatabaseFactory {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseFactory.class);
    private final Scheduler mainScheduler;
    private final Disposable databaseProbe;
    private Scheduler mySQLScheduler;
    private ConnectingPool connectingPool;
    private static ConnectingPoolType DATABASE_TYPE_CONNECTING_POOL = ConnectingPoolType.TOMCAT;

    public DatabaseFactory(Executor mainExecutor, String configFilePath) {
        this.mainScheduler = Schedulers.from(mainExecutor);
        ServerProperties serverProperties = new ServerProperties(configFilePath);
        mySQLScheduler = Schedulers.from(Executors.newFixedThreadPool(serverProperties.getInteger("nThreads", 16), new CountedThreadFactory("DB-Thread")));
        connectingPool = DATABASE_TYPE_CONNECTING_POOL.newInstance(serverProperties);
        logger.info(getStats());
        Duration duration = Duration.ofMinutes(5);
        databaseProbe = Observable.interval(duration.toMillis(), TimeUnit.MILLISECONDS)
                .doOnNext(aLong -> logger.info("[PROBE] Test getting communication connection"))
                .flatMapSingle(aLong -> getConnectionSRx())
                .map(RxConnection::getSqlConnection)
                .doOnNext(connection -> logger.info(getStats()))
                .doOnNext(Connection::close)
                .subscribeOn(mainScheduler)
                .doOnError(throwable -> logger.warn("[PROBE] Connecting from test not closed!", throwable))
                .subscribe();
    }


    public Transaction createTransaction() {
        return new Transaction(getConnectionSRx());
    }

    public Single<RxConnection> getConnectionSRx() {
        return Single
                .fromCallable(() -> RxConnection.create(connectingPool.getConnection(), mainScheduler))
                .subscribeOn(mySQLScheduler);
    }

    public void shutdown() {
        try {
            databaseProbe.dispose();
            connectingPool.shutdown();
            connectingPool = null;
        } catch (final Exception e) {
            logger.error("", e);
        }
    }

    public String getStats() {
        return connectingPool.getStats();
    }
}
