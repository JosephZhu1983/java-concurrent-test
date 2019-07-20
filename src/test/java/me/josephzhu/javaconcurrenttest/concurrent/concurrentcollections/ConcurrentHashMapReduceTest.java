package me.josephzhu.javaconcurrenttest.concurrent.concurrentcollections;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Slf4j
public class ConcurrentHashMapReduceTest {

    int loopCount = 100;
    int itemCount = 10000000;

    @Test
    public void test() {
        ConcurrentHashMap<String, Long> concurrentHashMap = LongStream.rangeClosed(1, itemCount)
                .boxed()
                .collect(Collectors.toMap(i -> "item" + i, Function.identity(),
                        (o1, o2) -> o1, ConcurrentHashMap::new));

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("normal");
        normal(concurrentHashMap);
        stopWatch.stop();
        stopWatch.start("concurrent with parallelismThreshold=1");
        concurrent(concurrentHashMap, 1);
        stopWatch.stop();
        stopWatch.start("concurrent with parallelismThreshold=max long");
        concurrent(concurrentHashMap, Long.MAX_VALUE);
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
    }

    private void normal(ConcurrentHashMap<String, Long> map) {
        IntStream.rangeClosed(1, loopCount).forEach(__ -> {
            long sum = 0L;
            for (Map.Entry<String, Long> item : map.entrySet()) {
                sum += item.getValue();
            }
            double average = sum / map.size();
            Assert.assertEquals(itemCount / 2, average, 0);
        });
    }

    private void concurrent(ConcurrentHashMap<String, Long> map, long parallelismThreshold) {
        IntStream.rangeClosed(1, loopCount).forEach(__ -> {
            double average = map.reduceEntriesToLong(parallelismThreshold, Map.Entry::getValue, 0, Long::sum) / map.size();
            Assert.assertEquals(itemCount / 2, average, 0);
        });
    }
}
