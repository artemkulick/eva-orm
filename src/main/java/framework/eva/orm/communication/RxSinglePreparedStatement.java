package framework.eva.orm.communication;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.functions.Action;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RxSinglePreparedStatement
{
    private Scheduler mainScheduler;
    private Action closeConnection;
    private PreparedStatement prepareStatement;

    private RxSinglePreparedStatement(PreparedStatement prepareStatement, Scheduler mainScheduler, Action closeConnection)
    {
        this.prepareStatement = prepareStatement;
        this.mainScheduler = mainScheduler;
        this.closeConnection = closeConnection;
    }

    public static RxSinglePreparedStatement create(PreparedStatement prepareStatement, Scheduler mainScheduler, Action closeConnection)
    {
        return new RxSinglePreparedStatement(prepareStatement, mainScheduler, closeConnection);
    }

    Single<ResultSet> executeQuerySRx()
    {
        return Single
                .fromCallable(() -> prepareStatement.executeQuery())
                .observeOn(mainScheduler);
//                .doFinally(() -> closeAsync().subscribe()); //todo wrap to transaction
    }

    Single<Integer> executeUpdateSRx()
    {
        return Single
                .fromCallable(() -> prepareStatement.executeUpdate())
                .observeOn(mainScheduler);
//                .doFinally(() -> closeAsync().subscribe()); //todo wrap to transaction
    }

    public Completable closeAsync()
    {
        return Completable
                .fromAction(() -> {
                    prepareStatement.close();
                    closeConnection.run();
                });
    }
}
