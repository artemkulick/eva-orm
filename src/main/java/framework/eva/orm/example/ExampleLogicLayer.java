package framework.eva.orm.example;

import framework.eva.orm.communication.TransactionManager;
import io.reactivex.Completable;

public class ExampleLogicLayer {

    private ExampleDAOLayer daoLayer;

    public ExampleLogicLayer(ExampleDAOLayer daoLayer) {
        this.daoLayer = daoLayer;
    }

    public Completable replaceAllUserNamesToX() {
        return TransactionManager
                .wrapCompletable(transaction -> daoLayer
                        .getUsers(transaction)
                        .doOnNext(user -> user.name = "x")
                        .flatMapCompletable(user -> daoLayer.updateUser(transaction, user))
                );
    }
}
