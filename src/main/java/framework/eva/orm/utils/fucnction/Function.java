package framework.eva.orm.utils.fucnction;

public interface Function<T, R>
{
    R apply(T t);
}
