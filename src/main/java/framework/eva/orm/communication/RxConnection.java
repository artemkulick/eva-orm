package framework.eva.orm.communication;

import framework.eva.orm.builder.SqlRequest;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;

import java.sql.Connection;

public class RxConnection {
    private final Connection sqlConnection;
    private Scheduler mainScheduler;

    private RxConnection(Connection sqlConnection, Scheduler mainScheduler) {
        this.sqlConnection = sqlConnection;
        this.mainScheduler = mainScheduler;
    }

    public static RxConnection create(Connection connection, Scheduler mainScheduler) {
        return new RxConnection(connection, mainScheduler);
    }

    public Connection getSqlConnection() {
        return sqlConnection;
    }

    public Single<RxSinglePreparedStatement> prepareStatementSRx(SqlRequest request) {
        return prepareStatementSRx(request.toString());
    }

    public Single<RxSinglePreparedStatement> prepareStatementSRx(String sql) {
        return Single
                .fromCallable(() -> RxSinglePreparedStatement.create(sqlConnection.prepareStatement(sql), mainScheduler, sqlConnection::close));
    }

}
