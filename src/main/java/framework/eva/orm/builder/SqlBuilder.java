package framework.eva.orm.builder;

import framework.eva.orm.communication.Transaction;
import framework.eva.orm.table.Column;
import framework.eva.orm.table.Row;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import java.util.List;

public interface SqlBuilder<T> {
    SqlRequest build() throws SqlBuilderException;

    List<Column> getColumns();

    default Maybe<T> buildTypedMRx(Transaction transaction) {
        return Maybe.error(new RuntimeException("buildTypedMRx not implemented for " + this.getClass().getSimpleName() + " request."));
    }

    default Completable buildCRx(Transaction transaction) {
        return Completable.error(new RuntimeException("buildCRx not implemented for " + this.getClass().getSimpleName() + " request."));
    }

    default Maybe<Row> buildMRx(Transaction transaction) {
        return Maybe.error(new RuntimeException("buildMRx not implemented for " + this.getClass().getSimpleName() + " request."));
    }

    default Single<Row> buildSRx(Transaction transaction) {
        return Single.error(new RuntimeException("buildSRx not implemented for " + this.getClass().getSimpleName() + " request."));
    }

    default Single<T> buildTypedSRx(Transaction transaction) {
        return Single.error(new RuntimeException("buildSRx not implemented for " + this.getClass().getSimpleName() + " request."));
    }

    default Observable<T> buildTypedORx(Transaction transaction) {
        return Observable.error(new RuntimeException("buildTypedORx  not implemented for " + this.getClass().getSimpleName() + " request."));
    }
}
