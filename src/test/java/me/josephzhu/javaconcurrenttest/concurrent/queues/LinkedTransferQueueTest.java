package me.josephzhu.javaconcurrenttest.concurrent.queues;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.concurrent.*;
import java.util.stream.IntStream;

@Slf4j
public class LinkedTransferQueueTest {

    LinkedTransferQueue<String> linkedTransferQueue = new LinkedTransferQueue<>();
    LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<>();

    int taskCount = 1000000;
    int threadCount = 10;

    @Test
    public void test() throws InterruptedException {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start("linkedTransferQueue");
        ExecutorService producer1 = Executors.newFixedThreadPool(threadCount);
        IntStream.rangeClosed(1, taskCount).forEach(i -> producer1.submit(() -> {
            try {
                linkedTransferQueue.transfer("message" + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        ExecutorService consumer1 = Executors.newFixedThreadPool(threadCount);
        IntStream.rangeClosed(1, taskCount).forEach(i -> consumer1.submit(() -> {
            try {
                linkedTransferQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        producer1.shutdown();
        producer1.awaitTermination(1, TimeUnit.HOURS);
        consumer1.shutdown();
        consumer1.awaitTermination(1, TimeUnit.HOURS);
        stopwatch.stop();
        stopwatch.start("linkedBlockingQueue");
        ExecutorService producer2 = Executors.newFixedThreadPool(threadCount);
        IntStream.rangeClosed(1, taskCount).forEach(i -> producer2.submit(() -> {
            try {
                linkedBlockingQueue.put("message" + i);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        ExecutorService consumer2 = Executors.newFixedThreadPool(threadCount);
        IntStream.rangeClosed(1, taskCount).forEach(i -> consumer2.submit(() -> {
            try {
                linkedBlockingQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        producer2.shutdown();
        producer2.awaitTermination(1, TimeUnit.HOURS);
        consumer2.shutdown();
        consumer2.awaitTermination(1, TimeUnit.HOURS);
        stopwatch.stop();
        log.info(stopwatch.prettyPrint());
    }
}
