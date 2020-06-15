package framework.eva.orm.table;

public class TableCreator
{
    public static <T> Table<T>.Setup setup(String name)
    {
        return new Table<T>(name).setup(name);
    }
}
