package me.josephzhu.javaconcurrenttest.concurrent.concurrentcollections;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Slf4j
public class ConcurrentHashMapMisUse {

    @Test
    public void test2() {
        Map<String, String> map = new HashMap<>();
        map.put("aa", null);
        Map<Long, Long> concurrentMap = LongStream.rangeClosed(1, 10)
                .boxed()
                .collect(Collectors.toConcurrentMap(Function.identity(), Function.identity()));
        log.debug(concurrentMap.getClass().toString());
    }

    @Test
    public void test() throws InterruptedException {
        int limit = 1000;
        ConcurrentHashMap<String, Long> concurrentHashMap = LongStream.rangeClosed(1, limit - 10)
                .boxed()
                .collect(Collectors.toConcurrentMap(i -> UUID.randomUUID().toString(), Function.identity(),
                        (o1, o2) -> o1, ConcurrentHashMap::new));
        log.info("init size:{}", concurrentHashMap.size());

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int __ = 0; __ < 10; __++) {
            executorService.execute(() -> {
                int gap = limit - concurrentHashMap.size();
                log.debug("gap:{}", gap);
                concurrentHashMap.putAll(LongStream.rangeClosed(1, gap)
                        .boxed()
                        .collect(Collectors.toMap(i -> UUID.randomUUID().toString(), Function.identity())));
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);

        log.info("finish size:{}", concurrentHashMap.size());
    }
}
