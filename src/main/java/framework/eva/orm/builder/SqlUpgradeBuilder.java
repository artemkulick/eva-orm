package framework.eva.orm.builder;

import framework.eva.orm.communication.Transaction;
import framework.eva.orm.table.Column;
import framework.eva.orm.table.ColumnType;
import framework.eva.orm.table.Table;
import io.reactivex.Completable;

import java.util.HashSet;

public class SqlUpgradeBuilder<T> extends SqlBuilderImpl<T>
{

    private final static String ALTER_TABLE = "ALTER TABLE ";
    private final static String ADD_COLUMN = "ADD COLUMN ";
    private final static String DROP_COLUMN = "DROP COLUMN ";
    private final HashSet<Column> columnsToAdd = new HashSet<>();
    private final HashSet<Column> columnsToRemove = new HashSet<>();
    private final Table<T> table;

    private SqlUpgradeBuilder(Table<T> table)
    {
        super(table.getColumns());
        this.table = table;
    }

    public static <T> SqlUpgradeBuilder create(Table<T> table)
    {
        return new SqlUpgradeBuilder<>(table);
    }

    public SqlUpgradeBuilder<T> addColumn(Column column)
    {
        columnsToAdd.add(column);
        return this;
    }

    public SqlUpgradeBuilder<T> dropColumn(Column column)
    {
        columnsToRemove.add(column);
        return this;
    }

    @Override
    public SqlRequest build() throws SqlBuilderException
    {
        StringBuilder builder = new StringBuilder();
        builder.append(ALTER_TABLE).append(table.tableName).append(SPACE);
        boolean first = true;
        for (Column column : columnsToAdd)
        {
            if (!first)
            {
                builder.append(COMMA).append(SPACE);
            }
            builder.append(ADD_COLUMN).append(columnToString(column));
            first = false;
        }

        first = true;

        for (Column column : columnsToRemove)
        {
            if (!first)
            {
                builder.append(COMMA).append(SPACE);
            }
            builder.append(DROP_COLUMN).append(column.columnName);
            first = false;
        }
        builder.append(SEMICOLON);

        return new SqlRequest(builder.toString());
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

    @Override
    public Completable buildCRx(Transaction databaseFactory)
    {
        return databaseFactory.wrapCRx(this);
    }
}
