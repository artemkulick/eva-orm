package framework.eva.orm.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class CountedThreadFactory implements ThreadFactory
{
    private final AtomicLong count = new AtomicLong();
    private String baseName;

    public CountedThreadFactory(String baseName)
    {
        this.baseName = baseName;
    }

    @Override
    public Thread newThread(Runnable r)
    {
        Thread thread = new Thread(r);
        thread.setName("Thread-" + baseName + "-[" + count.getAndIncrement() + "]");
        return thread;
    }
}
