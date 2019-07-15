package me.josephzhu.javaconcurrenttest.concurrent.concurrentcollections;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

@Slf4j
public class ConcurrentHashMapTest {

    int loopCount = 100000000;
    int threadCount = 8;
    int itemCount = 100;

    @Test
    public void test() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("normal");
        normal();
        stopWatch.stop();
        stopWatch.start("concurrent");
        concurrent();
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
    }

    private void normal() throws InterruptedException {
        Map<String, Long> freqs = new HashMap<>();
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadCount);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, loopCount).parallel().forEach(i -> {
                    String key = "item" + ThreadLocalRandom.current().nextInt(itemCount);
                    synchronized (freqs) {
                        if (freqs.containsKey(key)) {
                            freqs.put(key, freqs.get(key) + 1);
                        } else {
                            freqs.put(key, 1L);
                        }
                    }
                }
        ));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
    }

    private void concurrent() throws InterruptedException {
        Map<String, LongAdder> freqs = new ConcurrentHashMap<>();
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadCount);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, loopCount).parallel().forEach(i -> {
                    String key = "item" + ThreadLocalRandom.current().nextInt(itemCount);
                    freqs.computeIfAbsent(key, k -> new LongAdder()).increment();
                }
        ));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
    }
}
