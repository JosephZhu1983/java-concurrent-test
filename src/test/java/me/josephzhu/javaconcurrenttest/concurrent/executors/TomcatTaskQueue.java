package me.josephzhu.javaconcurrenttest.concurrent.executors;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TomcatTaskQueue extends LinkedBlockingQueue<Runnable> {

    private transient volatile TomcatThreadPool parent = null;

    public TomcatTaskQueue(int capacity) {
        super(capacity);
    }

    public void setParent(TomcatThreadPool tp) {
        parent = tp;
    }

    public boolean force(Runnable o) {
        if (parent == null || parent.isShutdown())
            throw new RejectedExecutionException("taskQueue.notRunning");
        return super.offer(o); //forces the item onto the queue, to be used if the task is rejected
    }

    public boolean force(Runnable o, long timeout, TimeUnit unit) throws InterruptedException {
        if (parent == null || parent.isShutdown())
            throw new RejectedExecutionException("taskQueue.notRunning");
        return super.offer(o, timeout, unit); //forces the item onto the queue, to be used if the task is rejected
    }

    @Override
    public boolean offer(Runnable o) {
        //we can't do any checks
        if (parent == null) return super.offer(o);
        //we are maxed out on threads, simply queue the object
        if (parent.getPoolSize() == parent.getMaximumPoolSize()) return super.offer(o);
        //we have idle threads, just add it to the queue
        if (parent.getSubmittedCount() <= (parent.getPoolSize())) return super.offer(o);
        //if we have less threads than maximum force creation of a new thread
        if (parent.getPoolSize() < parent.getMaximumPoolSize()) {
            log.info("Grow thread pool, getPoolSize: {}, getMaximumPoolSize:{}", parent.getPoolSize(), parent.getMaximumPoolSize());
            return false;
        }
        //if we reached here, we need to add it to the queue
        return super.offer(o);
    }
}
