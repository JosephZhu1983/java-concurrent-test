package me.josephzhu.javaconcurrenttest.concurrent.queues;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

@Slf4j
public class BlockingQueueBenchmark {

    int taskCount = 10000000;
    int threadCount = 10;

    @Test
    public void test() throws InterruptedException {

        List<BlockingQueue<Integer>> queues = getQueues();
        benchmark("put", queues, taskCount, threadCount);
        System.gc();
        benchmark("take", queues, taskCount, threadCount);
    }

    private List<BlockingQueue<Integer>> getQueues() {
        return Arrays.asList(
                new LinkedBlockingQueue<>(),
                new LinkedTransferQueue<>(),
                new ArrayBlockingQueue<>(taskCount, false),
                new PriorityBlockingQueue<>());
    }

    private void benchmark(String operation, List<BlockingQueue<Integer>> queues, int taskCount, int threadCount) throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        queues.forEach(queue -> {
            stopWatch.start(queue.getClass().getSimpleName() + "-" + operation);
            try {
                tasks(queue, taskCount, threadCount, operation);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stopWatch.stop();
            log.info("queue:{}, operation:{}, size:{}", queue.getClass().getSimpleName(), operation, queue.size());
        });
        log.info(stopWatch.prettyPrint());
    }

    private void tasks(BlockingQueue<Integer> queue, int taskCount, int threadCount, String operation) throws InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadCount);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, taskCount).parallel().forEach(task(queue, operation)));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
    }

    private IntConsumer task(BlockingQueue<Integer> queue, String name) {
        if (name.equals("put")) return i -> {
            try {
                queue.put(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        if (name.equals("take")) return i -> {
            try {
                queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        return i -> {
        };
    }
}
