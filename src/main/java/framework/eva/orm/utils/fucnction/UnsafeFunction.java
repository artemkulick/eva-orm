package framework.eva.orm.utils.fucnction;

public interface UnsafeFunction<T, R>
{
    R apply(T t) throws Exception;
}
