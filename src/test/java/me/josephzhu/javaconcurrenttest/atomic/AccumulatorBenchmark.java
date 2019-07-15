package me.josephzhu.javaconcurrenttest.atomic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

@Slf4j
public class AccumulatorBenchmark {

    private StopWatch stopWatch = new StopWatch();
    private Map<String, IntConsumer> tasks = new HashMap<>();

    @Test
    public void test() {

        int threadCount = 100;
        int taskCount = 1000000000;
        AtomicLong atomicLong = new AtomicLong();
        LongAdder longAdder = new LongAdder();
        LongAccumulator longAccumulator = new LongAccumulator(Long::sum, 0L);
        tasks.put("atomicLong", i -> atomicLong.incrementAndGet());
        tasks.put("longAdder", i -> longAdder.increment());
        tasks.put("longAccumulator", i -> longAccumulator.accumulate(1L));
        tasks.entrySet().forEach(item -> benchmark(threadCount, taskCount, item.getValue(), item.getKey()));

        log.info(stopWatch.prettyPrint());
        Assert.assertEquals(taskCount, atomicLong.get());
        Assert.assertEquals(taskCount, longAdder.longValue());
        Assert.assertEquals(taskCount, longAccumulator.longValue());

    }

    private void benchmark(int threadCount, int taskCount, IntConsumer task, String name) {
        stopWatch.start(name);
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadCount);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, taskCount).parallel().forEach(task));
        forkJoinPool.shutdown();
        try {
            forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopWatch.stop();
    }
}
