package framework.eva.orm.builder;


import framework.eva.orm.communication.Transaction;
import framework.eva.orm.table.Table;
import framework.eva.orm.utils.StringUtils;
import io.reactivex.Completable;

public class SqlDeleteBuilder extends SqlBuilderImpl {
    private static final String DELETE = "DELETE";
    private static final String FROM = "FROM";

    private Table table;

    private SqlDeleteBuilder(Table table) {
        super(null);
        this.table = table;
    }

    public static SqlDeleteBuilder create(Table table) {
        return new SqlDeleteBuilder(table);
    }

    @Override
    public SqlRequest build() throws SqlBuilderException {
        StringBuilder builder = new StringBuilder(DELETE).append(SPACE).append(FROM).append(SPACE);

        if (StringUtils.isEmpty(table.tableName)) {
            throw new SqlBuilderException("Table cannot be empty. ");
        } else {
            builder.append(table.tableName).append(SPACE).append(conditionString).append(SEMICOLON);
        }

        return new SqlRequest(builder.toString());
    }

    @Override
    public Completable buildCRx(Transaction transaction) {
        return transaction.wrapCRx(this);
    }
}
