package framework.eva.orm.builder;

import framework.eva.orm.table.Column;
import framework.eva.orm.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

public abstract class SqlBuilderImpl<T> implements SqlBuilder<T>
{
    final static String WHERE = "WHERE";
    final static String AND = "AND";
    final static String OR = "OR";
    final static char SPACE = ' ';
    final static char SEMICOLON = ';';
    final static char APOSTROPHE = '\'';
    final static char CLOSE_BKT = ')';
    final static char OPEN_BKT = '(';
    final static char COMMA = ',';
    final static char ASSIGN = '=';

    List<Column> columns;

    StringBuilder conditionString;

    SqlBuilderImpl(List<Column> columns)
    {
        this.columns = columns;
        this.conditionString = new StringBuilder();
    }

    public SqlBuilder<T> withCondition(Condition condition)
    {
        conditionString.append(WHERE).append(SPACE).append(OPEN_BKT).append(condition.key).append(SPACE).append(condition.condition).append(SPACE).append(condition.values).append(CLOSE_BKT);
        return this;
    }

    public SqlBuilder<T> withConditions(Condition... cond)
    {
        List<Condition> conditions = Arrays.asList(cond);
        if (!conditions.isEmpty())
        {
            conditionString.append(WHERE).append(SPACE).append(OPEN_BKT).append(conditions.get(0).key).append(SPACE).append(conditions.get(0).condition).append(SPACE).append(conditions.get(0).values).append(CLOSE_BKT);
            if (conditions.size() > 1)
            {
                for (int i = 1; i < conditions.size(); i++)
                {
                    conditionString.append(SPACE).append(join(ConditionJoinType.AND)).append(SPACE).append(OPEN_BKT).append(conditions.get(i).key).append(SPACE).append(conditions.get(i).condition).append(SPACE).append(conditions.get(i).values).append(CLOSE_BKT);
                }
            }
        }
        return this;
    }

    public String join(ConditionJoinType type)
    {
        switch (type)
        {
        case NONE:
            return "";
        case OR:
            return "OR";
        case AND:
        default:
            return "AND";
        }
    }

    @Override
    public abstract SqlRequest build() throws SqlBuilderException;

    @Override
    public List<Column> getColumns()
    {
        return columns;
    }

    public static class Assignment
    {
        String key;
        String value;

        private Assignment(String key, String value)
        {
            this.key = key;
            this.value = value;
        }

        public static Assignment create(String columnName, String value)
        {
            return new Assignment(columnName, value);
        }
    }

    public static class Condition
    {
        ConditionJoinType type;
        String key;
        String condition;
        String values;

        public Condition create(ConditionJoinType type, String key, String condition, String values)
        {
            return new Condition(type, key, condition, values);
        }

        private Condition(ConditionJoinType type, String key, String condition, String values)
        {
            this.type = type;
            this.key = key;
            this.condition = condition;
            this.values = values;
        }


    }

    public static class ConditionBuilder
    {
        private ConditionJoinType type = ConditionJoinType.NONE;
        private String key;
        private String condition;
        private String values;

        public ConditionBuilder join(ConditionJoinType type)
        {
            this.type = type;
            return this;
        }

        public ConditionBuilder setCondition(SqlConditionType condition)
        {
            switch (condition)
            {

            case EQUALS:
                this.condition = "=";
                break;
            case NOT_EQUALS:
                this.condition = "<>";
                break;
            case GREATER_THAN:
                this.condition = ">";
                break;
            case LESS_THAN:
                this.condition = "<";
                break;
            case GREATER_OR_EQUAL:
                this.condition = ">=";
                break;
            case LESS_OR_EQUAL:
                this.condition = "<=";
                break;
            case BETWEEN:
                this.condition = "BETWEEN";
                break;
            case NOT_BETWEEN:
                this.condition = "NOT BETWEEN";
                break;
            case LIKE:
                this.condition = "LIKE";
                break;
            case NOT_LIKE:
                this.condition = "NOT LIKE";
                break;
            case IN:
                this.condition = "IN";
                break;
            case NOT_IN:
                this.condition = "NOT IN";
                break;
            }
            return this;
        }

        public ConditionBuilder setConditionKey(String key)
        {
            this.key = key;
            return this;
        }

        public ConditionBuilder setConditionValues(String... values)
        {
            if (values.length == 1)
            {
                this.values = APOSTROPHE + values[0] + APOSTROPHE;
            }
            StringBuilder builder = new StringBuilder();
            if (values.length == 2)
            {
                this.values = builder
                        .append(APOSTROPHE)
                        .append(values[0])
                        .append(APOSTROPHE)
                        .append(SPACE)
                        .append(AND)
                        .append(SPACE)
                        .append(APOSTROPHE)
                        .append(values[1])
                        .append(APOSTROPHE)
                        .toString();
            }
            if (values.length > 2)
            {
                builder.append(OPEN_BKT).append(APOSTROPHE).append(values[0]).append(APOSTROPHE);
                for (int i = 1; i < values.length; i++)
                {
                    builder.append(COMMA).append(APOSTROPHE).append(values[i]).append(APOSTROPHE);
                }
                this.values = builder.append(CLOSE_BKT).toString();
            }
            return this;
        }

        public Condition build() throws SqlBuilderException
        {
            if (StringUtils.isEmpty(this.key) || StringUtils.isEmpty(this.condition) || StringUtils.isEmpty(this.values))
            {
                throw new SqlBuilderException("Not enough parameters.");
            }
            else
            {
                return new Condition(this.type, this.key, this.condition, this.values);
            }
        }
    }

    public enum ConditionJoinType
    {
        NONE, OR, AND
    }
}
