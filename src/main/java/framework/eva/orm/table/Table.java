package framework.eva.orm.table;

import framework.eva.orm.builder.*;
import framework.eva.orm.utils.fucnction.Function;
import framework.eva.orm.utils.fucnction.UnsafeFunction;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Table<V> {
    public String tableName;
    private List<Column> columns = new LinkedList<>();
    private Function<Row, V> builderFunction;
    private UnsafeFunction<Row, V> unsafeBuilderFunction;
    private Class<V> clazz;
    private Column<?> primaryColumn;

    Table(String name) {
        tableName = name;
    }

    public Setup setup(String name) {
        return new Table<V>(name).setup();
    }

    public SqlSelectBuilder<V> select() {
        return SqlSelectBuilder.create(this, Collections.emptyList(), builderFunction, unsafeBuilderFunction, clazz);
    }

    public SqlSelectBuilder<V> select(Column column) {
        return select(Collections.singletonList(column));
    }


    public SqlSelectBuilder<V> select(List<Column> columns) {
        return SqlSelectBuilder.create(this, columns, null, null, null);
    }

    public SqlInsertBuilder<V> insert(SqlBuilderImpl.Assignment... assignments) {
        return SqlInsertBuilder.create(this, Arrays.asList(assignments));
    }

    public SqlUpdateBuilder update(SqlBuilderImpl.Assignment... assignments) {
        return SqlUpdateBuilder.create(this, Arrays.asList(assignments));
    }

    public SqlDeleteBuilder delete() {
        return SqlDeleteBuilder.create(this);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public SqlCreateBuilder create() {
        return SqlCreateBuilder.create(this);
    }

    public SqlUpgradeBuilder upgrade() {
        return SqlUpgradeBuilder.create(this);
    }

    public Column getPrimaryColumn() {
        return primaryColumn;
    }

    private Setup setup() {
        return new Setup();
    }

    public class Setup {
        public Table<V> done() {
            return Table.this;
        }

        public Setup addColumn(Column<?> column) {
            columns.add(column);
            return this;
        }

        public Setup addPrimaryColumn(Column<?> column) {
            columns.add(column);
            primaryColumn = column;
            return this;
        }


        public Setup addBuilder(Function<Row, V> function) {
            builderFunction = function;
            return this;
        }

        public Setup addUnsafeBuilder(UnsafeFunction<Row, V> function) {
            unsafeBuilderFunction = function;
            return this;
        }

        public Setup parameterization(Class<V> c) {
            clazz = c;
            return this;
        }
    }
}
