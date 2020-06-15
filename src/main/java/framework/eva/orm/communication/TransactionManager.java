package framework.eva.orm.communication;

import framework.eva.orm.utils.fucnction.Function;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class TransactionManager {

    private static DatabaseFactory factory; //todo injection

    public static <T> Single<T> wrapSingle(Function<Transaction, Single<T>> func){
        Transaction transaction = factory.createTransaction();
        return func.apply(transaction)
                .doOnError(throwable -> transaction.rollback())
                .doFinally(transaction::commit);
    }

    public static <T> Observable<T> wrapObservable(Function<Transaction, Observable<T>> func){
        Transaction transaction = factory.createTransaction();
        return func.apply(transaction)
                .doOnError(throwable -> transaction.rollback())
                .doOnComplete(transaction::commit);
    }

    public static Completable wrapCompletable(Function<Transaction, Completable> func){
        Transaction transaction = factory.createTransaction();
        return func.apply(transaction)
                .doOnError(throwable -> transaction.rollback())
                .doOnComplete(transaction::commit);
    }

    public static void init(DatabaseFactory factory){
        TransactionManager.factory = factory;
    }
}

