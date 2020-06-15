package framework.eva.orm.table;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Row
{
    private HashMap<String, Object> results;

    private Row(HashMap<String, Object> results)
    {
        this.results = results;
    }

    public static Row create(ResultSet resultSet, List<Column> columns) throws SQLException
    {
        HashMap<String, Object> results = new HashMap<>();
        for (Column column : columns)
        {
            Object value = null;
            switch (column.columnType)
            {
            case UUID:
                value = UUID.fromString(resultSet.getString(column.columnName));
                break;
            case INTEGER:
                value = resultSet.getInt(column.columnName);
                break;
            case STRING:
                value = resultSet.getString(column.columnName);
                break;
            case LONG:
                value = resultSet.getLong(column.columnName);
                break;
            case DOUBLE:
                value = resultSet.getDouble(column.columnName);
                break;
            case BYTE:
                value = resultSet.getByte(column.columnName);
                break;
            case ENUM:
                value = resultSet.getString(column.columnName);
                break;
            }
            results.put(column.columnName, value);
        }
        return new Row(results);
    }

    public <T> T getValue(Column<T> column)
    {
        Object o = results.get(column.columnName);
        if (o != null)
        {
            return column.getValue(o);
        }
        else
        {
            return null;
        }
    }
}
