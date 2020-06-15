package framework.eva.orm.builder;

import framework.eva.orm.communication.Transaction;
import framework.eva.orm.table.Table;
import framework.eva.orm.utils.StringUtils;
import io.reactivex.Completable;

import java.util.LinkedList;
import java.util.List;

public class SqlInsertBuilder<T> extends SqlBuilderImpl<T>
{
    private static final String INSERT_INTO = "INSERT INTO";
    private static final String VALUES = "VALUES";
    private static final char COMMA = ',';
    private static final char APOSTROPHE = '\'';
    private static final char SPACE = ' ';
    private static final char SEMICOLON = ';';
    private static final char OPEN_BKT = '(';
    private static final char CLOSE_BKT = ')';

    private Table table;
    private List<String> fields = new LinkedList<>();
    private List<String> values = new LinkedList<>();

    private SqlInsertBuilder(Table table, List<Assignment> assignments)
    {
        super(null);
        for (Assignment assignment : assignments)
        {
            fields.add(assignment.key);
            values.add(assignment.value);
        }
        this.table = table;
    }

    public static <T> SqlInsertBuilder<T> create(Table<T> table, List<Assignment> assignments)
    {
        return new SqlInsertBuilder<>(table, assignments);
    }

    @Override
    public SqlRequest build() throws SqlBuilderException
    {
        String tableName = table.tableName;
        if ((StringUtils.isEmpty(tableName)) || (this.fields.size() > 0 && this.fields.size() != this.values.size()))
        {
            throw new SqlBuilderException("Incorrect fields or values number");
        }
        else
        {
            StringBuilder builder = new StringBuilder(INSERT_INTO).append(SPACE).append(tableName).append(SPACE);

            if (!this.fields.isEmpty())
            {
                builder.append(OPEN_BKT).append(fields.get(0));
                if (fields.size() > 1)
                {
                    for (int i = 1; i < fields.size(); i++)
                    {
                        builder.append(COMMA).append(fields.get(i));
                    }
                }
                builder.append(CLOSE_BKT);
            }
            builder.append(SPACE).append(VALUES).append(SPACE).append(OPEN_BKT).append(APOSTROPHE).append(values.get(0)).append(APOSTROPHE);
            if (values.size() > 1)
            {
                for (int i = 1; i < values.size(); i++)
                {
                    builder.append(COMMA).append(APOSTROPHE).append(values.get(i)).append(APOSTROPHE);
                }
            }
            builder.append(CLOSE_BKT).append(SEMICOLON);
            return new SqlRequest(builder.toString());
        }
    }

    @Override
    public Completable buildCRx(Transaction transaction)
    {
        return transaction.wrapCRx(this);
    }

    public SqlBuilder withConditions(List<Condition> conditions)
    {
        return this;
    }
}
