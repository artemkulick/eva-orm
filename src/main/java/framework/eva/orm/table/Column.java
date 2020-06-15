package framework.eva.orm.table;

import framework.eva.orm.builder.SqlBuilderException;
import framework.eva.orm.builder.SqlBuilderImpl;
import framework.eva.orm.builder.SqlConditionType;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;

public class Column<T>
{
    public String columnName;
    public ColumnType columnType;
    private Function<String, T> creator;

    public Column(String columnName, ColumnType columnType, Function<String, T> creator)
    {
        this.columnName = columnName;
        this.columnType = columnType;
        this.creator = creator;
    }

    public static Column<UUID> UUIDColumn(String columnName)
    {
        return new Column<>(columnName, ColumnType.UUID, null);
    }

    public static Column<String> stringColumn(String columnName)
    {
        return new Column<>(columnName, ColumnType.STRING, null);
    }

    public static Column<Long> longColumn(String columnName)
    {
        return new Column<>(columnName, ColumnType.LONG, null);
    }

    public static Column<Byte> byteColumn(String columnName)
    {
        return new Column<>(columnName, ColumnType.BYTE, null);
    }

    public static Column<Double> doubleColumn(String columnName)
    {
        return new Column<>(columnName, ColumnType.DOUBLE, null);
    }

    public static <T> Column<T> enumColumn(String columnName, Function<String, T> getter)
    {
        return new Column<>(columnName, ColumnType.ENUM, getter);
    }

    public static Column<Integer> integerColumn(String columnName)
    {
        return new Column<>(columnName, ColumnType.INTEGER, null);
    }

    public SqlBuilderImpl.Assignment to(T value, Function<T, String> stringFunction)
    {
        return SqlBuilderImpl.Assignment.create(columnName, stringFunction.apply(value));
    }

    public SqlBuilderImpl.Assignment to(T value)
    {
        return to(value, String::valueOf);
    }

    public SqlBuilderImpl.Condition eq(Object... value) throws SqlBuilderException
    {
        return new SqlBuilderImpl
                .ConditionBuilder()
                .setCondition(SqlConditionType.EQUALS)
                .setConditionKey(columnName)
                .setConditionValues(Arrays.stream(value).map(String::valueOf).toArray(String[]::new))
                .build();
    }

    public SqlBuilderImpl.Condition lessThan(Object... value) throws SqlBuilderException
    {
        return new SqlBuilderImpl
                .ConditionBuilder()
                .setCondition(SqlConditionType.LESS_THAN)
                .setConditionKey(columnName)
                .setConditionValues(Arrays.stream(value).map(String::valueOf).toArray(String[]::new))
                .build();
    }

    public T getValue(Object o)
    {
        if (columnType == ColumnType.ENUM)
        {
            return creator.apply(String.valueOf(o));
        }
        return (T) o;
    }
}
