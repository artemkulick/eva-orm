package framework.eva.orm.builder;

import framework.eva.orm.communication.Transaction;
import framework.eva.orm.table.Table;
import framework.eva.orm.utils.StringUtils;
import io.reactivex.Completable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SqlUpdateBuilder extends SqlBuilderImpl
{
    private static final String UPDATE = "UPDATE";
    private static final String SET = "SET";

    private Table table;
    private Map<String, String> values = new HashMap<>();

    private SqlUpdateBuilder(Table table, List<Assignment> assignments)
    {
        super(null);
        for (Assignment assignment : assignments)
        {
            values.put(assignment.key, assignment.value);
        }
        this.table = table;
    }

    public static SqlUpdateBuilder create(Table table, List<Assignment> assignments)
    {
        return new SqlUpdateBuilder(table, assignments);
    }

    @Override
    public SqlRequest build() throws SqlBuilderException
    {
        StringBuilder builder = new StringBuilder(UPDATE).append(SPACE);
        if ((StringUtils.isEmpty(table.tableName)) || (this.values.size() == 0))
        {
            throw new SqlBuilderException("Incorrect fields or values number");
        }
        else
        {
            builder.append(table.tableName).append(SPACE).append(SET).append(SPACE).append(mapToString(values)).append(SPACE).append(conditionString).append(SEMICOLON);
        }
        return new SqlRequest(builder.toString());
    }

    private String mapToString(Map<String, String> values)
    {
        StringBuilder builder = new StringBuilder();
        AtomicInteger count = new AtomicInteger();
        values.forEach((key, value) -> {
            if (count.get() > 0)
            {
                builder.append(COMMA);
            }
            builder.append(key).append(ASSIGN).append(APOSTROPHE).append(value).append(APOSTROPHE);
            count.incrementAndGet();
        });
        return builder.toString();
    }

    @Override
    public Completable buildCRx(Transaction transaction)
    {
        return transaction.wrapCRx(this);
    }

}
