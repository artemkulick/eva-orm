package framework.eva.orm.builder;

import framework.eva.orm.communication.Transaction;
import framework.eva.orm.table.Column;
import framework.eva.orm.table.Row;
import framework.eva.orm.table.Table;
import framework.eva.orm.utils.StringUtils;
import framework.eva.orm.utils.fucnction.Function;
import framework.eva.orm.utils.fucnction.UnsafeFunction;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import java.util.List;

public class SqlSelectBuilder<T> extends SqlBuilderImpl<T> {
    private static final String SELECT = "SELECT";
    private static final String FROM = "FROM";
    private final Function<Row, T> builderFunction;
    private final UnsafeFunction<Row, T> unsafeFunction;
    private final Class<T> clazz;
    private String fields;
    private Table table;

    private SqlSelectBuilder(Table table, List<Column> selectedColumns, Function<Row, T> builderFunction, UnsafeFunction<Row, T> unsafeFunction, Class<T> clazz) {
        super(selectedColumns);
        if (selectedColumns.size() == 1) {
            setField(selectedColumns.get(0));
        } else {
            if (selectedColumns.size() > 1) {
                StringBuilder builder = new StringBuilder();
                boolean first = true;
                for (Column column : selectedColumns) {
                    if (first) {
                        builder.append(column.columnName);
                        first = false;
                    } else {
                        builder.append(COMMA).append(column.columnName);
                    }
                }
                fields = builder.toString();
            }
        }
        this.table = table;
        this.builderFunction = builderFunction;
        this.unsafeFunction = unsafeFunction;
        this.clazz = clazz;
    }

    public static <T> SqlSelectBuilder<T> create(Table table, List<Column> selectedColumns, Function<Row, T> builderFunction, UnsafeFunction<Row, T> unsafeFunction, Class<T> clazz) {
        return new SqlSelectBuilder<>(table, selectedColumns, builderFunction, unsafeFunction, clazz);
    }

    @Override
    public SqlRequest build() throws SqlBuilderException {
        StringBuilder builder = new StringBuilder(SELECT).append(SPACE);
        String tableName = table.tableName;
        if (StringUtils.isEmpty(tableName)) {
            throw new SqlBuilderException("Not enough parameters for building Sql request.");
        } else {
            if (StringUtils.isEmpty(this.fields)) {
                this.fields = "*";
            }
            builder.append(fields).append(SPACE).append(FROM).append(SPACE).append(tableName).append(SPACE).append(conditionString).append(SEMICOLON);
            return new SqlRequest(builder.toString());
        }
    }

    @Override
    public List<Column> getColumns() {
        return columns.size() > 0 ? columns : table.getColumns();
    }

    @Override
    public Maybe<Row> buildMRx(Transaction transaction) {
        return transaction.wrapMRx(this);
    }

    @Override
    public Maybe<T> buildTypedMRx(Transaction transaction) {
        return buildMRx(transaction)
                .map(row -> {
                    if (builderFunction != null) {
                        return builderFunction.apply(row);
                    } else {
                        return unsafeFunction.apply(row);
                    }
                });
    }

    @Override
    public Single<Row> buildSRx(Transaction transaction) {
        return transaction.wrapSRx(this);
    }

    @Override
    public Observable<T> buildTypedORx(Transaction transaction) {
        return transaction.wrapORx(this).map(row -> {
            if (builderFunction != null)
                return builderFunction.apply(row);
            if (unsafeFunction != null)
                return unsafeFunction.apply(row);
            if (clazz != null)
                return row.cast(clazz);
            else
                throw new RuntimeException("Not found type map function.");
        });
    }

    @Override
    public Single<T> buildTypedSRx(Transaction transaction) {
        return buildSRx(transaction)
                .map(row -> {
                    if (builderFunction != null) {
                        return builderFunction.apply(row);
                    } else {
                        return unsafeFunction.apply(row);
                    }
                })
                .onErrorResumeNext(throwable -> Single.error(new RuntimeException("Requested record in table [" + table.tableName + "] not founded with condition: " + conditionString)));
    }

    private void setField(Column column) {
        fields = column.columnName;
    }

}