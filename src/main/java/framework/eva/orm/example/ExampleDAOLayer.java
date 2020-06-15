package framework.eva.orm.example;

import framework.eva.orm.communication.Transaction;
import framework.eva.orm.table.Column;
import framework.eva.orm.table.Row;
import framework.eva.orm.table.Table;
import framework.eva.orm.table.TableCreator;
import io.reactivex.Completable;
import io.reactivex.Observable;

import java.util.UUID;

public class ExampleDAOLayer {
    private static Column<UUID> USER_ID_COLUMN = Column.UUIDColumn("id");
    private static Column<String> USER_NAME_COLUMN = Column.stringColumn("name");
    private static Column<String> USER_EMAIL_COLUMN = Column.stringColumn("email");
    private static Table<User> USER_TABLE = TableCreator.<User>setup("user_table")
            .addPrimaryColumn(USER_ID_COLUMN)
            .addColumn(USER_NAME_COLUMN)
            .addColumn(USER_EMAIL_COLUMN)
            .addBuilder(ExampleDAOLayer::mapUser)
            .done();

    public ExampleDAOLayer() {
    }

    public Observable<User> getUsers(Transaction transaction) {
        return USER_TABLE.select().buildTypedORx(transaction);
    }


    public Completable updateUser(Transaction transaction, User user) {
        return Completable.defer(() -> USER_TABLE
                .update(
                        USER_NAME_COLUMN.to(user.name),
                        USER_EMAIL_COLUMN.to(user.email),
                        USER_EMAIL_COLUMN.to(user.email),
                        USER_EMAIL_COLUMN.to(user.email),
                        USER_EMAIL_COLUMN.to(user.email),
                        USER_EMAIL_COLUMN.to(user.email)
                )
                .withCondition(USER_ID_COLUMN.eq(user.id))
                .buildCRx(transaction)
        );
    }

    private static User mapUser(Row row) {
        User user = new User();
        user.id = row.getValue(USER_ID_COLUMN);
        user.name = row.getValue(USER_NAME_COLUMN);
        user.email = row.getValue(USER_EMAIL_COLUMN);
        return user;
    }
}

