package me.josephzhu.javaconcurrenttest.concurrent.executors;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Slf4j
public class MultiThreadBenchmark {


    @Test
    public void test() throws InterruptedException {
        int taskCount = 100000000;
        int threadCount = 10;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("stream");
        Assert.assertEquals(taskCount, test1(taskCount, threadCount));
        stopWatch.stop();

        stopWatch.start("thread");
        Assert.assertEquals(taskCount, test2(taskCount, threadCount));
        stopWatch.stop();

        stopWatch.start("threadpool");
        Assert.assertEquals(taskCount, test3(taskCount, threadCount));
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());
    }


    private int test1(int taskCount, int threadCount) throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger();
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadCount);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, taskCount).parallel().forEach(i -> atomicInteger.incrementAndGet()));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        return atomicInteger.get();
    }

    private int test2(int taskCount, int threadCount) throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < taskCount / threadCount; j++) {
                    atomicInteger.incrementAndGet();
                }
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        return atomicInteger.get();
    }

    private int test3(int taskCount, int threadCount) throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        IntStream.rangeClosed(1, taskCount).forEach(i -> executorService.submit(atomicInteger::incrementAndGet));
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
        return atomicInteger.get();
    }
}
