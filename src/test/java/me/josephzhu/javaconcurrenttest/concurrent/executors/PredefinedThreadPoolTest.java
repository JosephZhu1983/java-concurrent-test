package me.josephzhu.javaconcurrenttest.concurrent.executors;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class PredefinedThreadPoolTest {

    private void printStats(ThreadPoolExecutor threadPool) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            log.info("=========================");
            log.info("Pool Size: {}", threadPool.getPoolSize());
            log.info("Active Threads: {}", threadPool.getActiveCount());
            log.info("Number of Tasks Completed: {}", threadPool.getCompletedTaskCount());
            log.info("Number of Tasks in Queue: {}", threadPool.getQueue().size());

            log.info("=========================");
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Test
    public void test() throws InterruptedException {

        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        printStats(threadPool);
        for (int i = 0; i < 100000000; i++) {
            threadPool.submit(() -> {
                String payload = IntStream.rangeClosed(1, 1000000).mapToObj(__ -> "a").collect(Collectors.joining("")) + UUID.randomUUID().toString();
                try {
                    TimeUnit.HOURS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info(payload);
            });
        }

        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.DAYS);
    }

    @Test
    public void test2() throws InterruptedException {

        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        printStats(threadPool);
        for (int i = 0; i < 100000000; i++) {
            threadPool.submit(() -> {
                String payload = IntStream.rangeClosed(1, 1000).mapToObj(__ -> "a").collect(Collectors.joining(""));
                try {
                    TimeUnit.HOURS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info(payload);
            });
        }
        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.DAYS);
    }
}
