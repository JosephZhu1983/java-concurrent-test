package me.josephzhu.javaconcurrenttest.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Slf4j
public class ThreadPoolExecutorTest {

    @Test
    public void testThreadPoolSize() throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger();
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(100) {
            @Override
            public boolean offer(Runnable e) {
                if (size() == 0) {
                    return super.offer(e);
                } else {
                    return false;
                }
            }
        };
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2,5,5, TimeUnit.SECONDS, queue);
        threadPool.setRejectedExecutionHandler((r, executor) -> {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        });
        threadPool.allowCoreThreadTimeOut(false);

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(()->{
            log.info("=========================");
            log.info("Pool Size:{}", threadPool.getPoolSize());
            log.info("Active Threads:{}", threadPool.getActiveCount());
            log.info("Number of Tasks Completed:{}", threadPool.getCompletedTaskCount());
            log.info("Totel Number of Tasks: {}", threadPool.getTaskCount());
            log.info("=========================");
        }, 0,1, TimeUnit.SECONDS);

            IntStream.rangeClosed(1,10).forEach(i-> {
//                try {
//                    TimeUnit.SECONDS.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                int id = atomicInteger.incrementAndGet();
            log.info("{} started", id);

                threadPool.submit(() -> {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("{} finished", id);
            });
        });
        //threadPool.shutdown();
        //threadPool.awaitTermination(1, TimeUnit.HOURS);

        Thread.sleep(60000);
    }
}