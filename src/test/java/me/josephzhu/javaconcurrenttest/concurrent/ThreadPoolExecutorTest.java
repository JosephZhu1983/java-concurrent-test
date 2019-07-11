package me.josephzhu.javaconcurrenttest.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

@Slf4j
public class ThreadPoolExecutorTest {

    @Test
    public void testThreadPoolSize() throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger();
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(20) {
            @Override
            public boolean offer(Runnable e) {
                if (size() == 0) {
                    return super.offer(e);
                } else {
                    return false;
                }
            }
        };

        class ThreadFactoryImpl implements ThreadFactory {
            private final AtomicLong threadIndex = new AtomicLong(0);
            private final String threadNamePrefix;
            private final boolean daemon;

            public ThreadFactoryImpl(final String threadNamePrefix) {
                this(threadNamePrefix, false);
            }

            public ThreadFactoryImpl(final String threadNamePrefix, boolean daemon) {
                this.threadNamePrefix = threadNamePrefix;
                this.daemon = daemon;
            }

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, threadNamePrefix + this.threadIndex.incrementAndGet());
                thread.setDaemon(daemon);
                return thread;
            }
        }

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, 5,
                5, TimeUnit.SECONDS,
                queue, new ThreadFactoryImpl("aa"), (r, executor) -> {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        threadPool.allowCoreThreadTimeOut(false);


        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            log.info("=========================");
            log.info("Pool Size:{}", threadPool.getPoolSize());
            log.info("Active Threads:{}", threadPool.getActiveCount());
            log.info("Number of Tasks Completed:{}", threadPool.getCompletedTaskCount());
            log.info("Totel Number of Tasks: {}", threadPool.getTaskCount());
            log.info("=========================");
        }, 0, 1, TimeUnit.SECONDS);

        IntStream.rangeClosed(1, 100).forEach(i -> {
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
