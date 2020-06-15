package framework.eva.orm.builder;

public class SqlRequest
{
    private String request;

    SqlRequest(String request)
    {
        this.request = request;
    }

    @Override
    public String toString()
    {
        return request;
    }
}
