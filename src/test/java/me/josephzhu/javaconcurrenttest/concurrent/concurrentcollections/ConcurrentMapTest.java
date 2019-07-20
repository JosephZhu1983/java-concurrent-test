package me.josephzhu.javaconcurrenttest.concurrent.concurrentcollections;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

@Slf4j
public class ConcurrentMapTest {

    int loopCount = 100000000;
    int threadCount = 1;
    int itemCount = 10000;

    @Test
    public void test() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("hashmap");
        normal();
        stopWatch.stop();
        stopWatch.start("concurrentHashMap");
        concurrent();
        stopWatch.stop();
        stopWatch.start("concurrentSkipListMap");
        concurrentSkipListMap();
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
    }

    private void normal() throws InterruptedException {
        HashMap<String, Long> freqs = new HashMap<>();
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
        //log.debug("normal:{}", freqs);

    }

    private void concurrent() throws InterruptedException {
        ConcurrentHashMap<String, LongAdder> freqs = new ConcurrentHashMap<>(itemCount);
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadCount);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, loopCount).parallel().forEach(i -> {
                    String key = "item" + ThreadLocalRandom.current().nextInt(itemCount);
                    freqs.computeIfAbsent(key, k -> new LongAdder()).increment();
                }
        ));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        //log.debug("concurrentHashMap:{}", freqs);
    }

    private void concurrentSkipListMap() throws InterruptedException {
        ConcurrentSkipListMap<String, LongAdder> freqs = new ConcurrentSkipListMap<>();
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadCount);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, loopCount).parallel().forEach(i -> {
                    String key = "item" + ThreadLocalRandom.current().nextInt(itemCount);
                    freqs.computeIfAbsent(key, k -> new LongAdder()).increment();
                }
        ));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        //log.debug("concurrentSkipListMap:{}", freqs);
    }
}
