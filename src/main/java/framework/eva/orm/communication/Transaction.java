package framework.eva.orm.communication;

import framework.eva.orm.builder.SqlBuilder;
import framework.eva.orm.builder.SqlSelectBuilder;
import framework.eva.orm.table.Row;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public class Transaction {
    private final Single<RxConnection> connectionSingle;
    private final AtomicReference<RxConnection> connection = new AtomicReference<>();

    Transaction(Single<RxConnection> connectionSingle) {
        this.connectionSingle = connectionSingle.doOnSuccess(rxConnection -> connection.set(rxConnection)).cache();
    }

    public Completable wrapCRx(SqlBuilder builder) {
        return connectionSingle
                .flatMapCompletable(rxConnection ->
                        rxConnection
                                .prepareStatementSRx(builder.build())
                                .flatMap(RxSinglePreparedStatement::executeUpdateSRx)
                                .ignoreElement()
                );
    }

    public Maybe<Row> wrapMRx(SqlBuilder builder) {
        return connectionSingle
                .flatMapMaybe(rc ->
                        rc.prepareStatementSRx(builder.build())
                                .flatMap(RxSinglePreparedStatement::executeQuerySRx)
                                .filter(ResultSet::next)
                                .map(resultSet -> Row.create(resultSet, builder.getColumns()))
                );
    }

    public Single<Row> wrapSRx(SqlBuilder builder) {
        return wrapMRx(builder).toSingle();
    }

    public Observable<Row> wrapORx(SqlBuilder builder) {

        return Observable.create(emitter ->
        {
            Disposable subscribe = connectionSingle
                    .flatMap(rc -> rc
                            .prepareStatementSRx(builder.build())
                            .flatMap(RxSinglePreparedStatement::executeQuerySRx))
//                    .subscribeOn(mainScheduler) //todo think about swap to main threads
                    .subscribe((resultSet) -> {
                                while (resultSet.next()) {
                                    emitter.onNext(Row.create(resultSet, builder.getColumns()));
                                }
                                emitter.onComplete();

                            },
                            emitter::onError);
        });
    }

    public void close() throws SQLException {
        connection.get().getSqlConnection().close();
    }

    public void commit() throws SQLException {
        connection.get().getSqlConnection().commit();
    }

    public void rollback() throws SQLException {
        connection.get().getSqlConnection().rollback();
    }
}
