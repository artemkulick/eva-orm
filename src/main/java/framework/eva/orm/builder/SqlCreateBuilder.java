package framework.eva.orm.builder;

import framework.eva.orm.communication.Transaction;
import framework.eva.orm.table.Column;
import framework.eva.orm.table.ColumnType;
import framework.eva.orm.table.Table;
import io.reactivex.Completable;

public class SqlCreateBuilder<T> extends SqlBuilderImpl<T>
{
    private final static String CREATE_TABLE = "CREATE TABLE";
    private final static String IF_NOT_EXISTS = "IF NOT EXISTS";
    private final static String PRIMARY_KEY = "PRIMARY KEY";

    private Table<T> table;
    private boolean ifNotExists = true;

    public SqlCreateBuilder(Table<T> table)
    {
        super(table.getColumns());
        this.table = table;
    }

    public static <T> SqlCreateBuilder create(Table<T> table)
    {
        return new SqlCreateBuilder<>(table);
    }

    @Override
    public SqlRequest build() throws SqlBuilderException
    {
        StringBuilder builder = new StringBuilder();
        builder.append(CREATE_TABLE).append(SPACE);
        if (ifNotExists)
        {
            builder.append(IF_NOT_EXISTS).append(SPACE);
        }
        builder.append(table.tableName).append(SPACE).append(OPEN_BKT);

        boolean first = true;
        for (Column column : table.getColumns())
        {
            if (!first)
            {
                builder.append(COMMA).append(SPACE);
            }
            else
            {
                first = false;
            }
            builder.append(columnToString(column));
        }

        Column primaryColumn = table.getPrimaryColumn();
        if (primaryColumn != null)
        {
            builder.append(COMMA).append(SPACE).append(PRIMARY_KEY).append(SPACE).append(OPEN_BKT).append(primaryColumn.columnName).append(CLOSE_BKT);
        }
        builder.append(CLOSE_BKT).append(SEMICOLON);
        return new SqlRequest(builder.toString());
    }

    public Completable buildCRx(Transaction transaction)
    {
        return transaction.wrapCRx(this);
    }

    public SqlBuilder<T> ifNotExists(boolean value)
    {
        ifNotExists = value;
        return this;
    }

    private StringBuilder columnToString(Column column)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(column.columnName).append(SPACE).append(getTypeString(column.columnType));
        return builder;
    }

    private String getTypeString(ColumnType columnType)
    {
        switch (columnType)
        {

        case UUID:
            return "VARCHAR(255)";
        case INTEGER:
            return "INT";
        case STRING:
            return "VARCHAR(255)";
        case LONG:
            return "BIGINT";
        case BYTE:
            return "TINYINT";
        case DOUBLE:
            return "DOUBLE";
        case ENUM:
            return "VARCHAR(255)";
        }
        throw new IllegalArgumentException("Unknown column type when creating table!" + columnType);
    }
}
