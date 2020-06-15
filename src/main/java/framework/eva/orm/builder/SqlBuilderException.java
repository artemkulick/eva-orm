package framework.eva.orm.builder;

public class SqlBuilderException extends Exception
{
    public SqlBuilderException(String message)
    {
        super(message);
    }

    public SqlBuilderException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
