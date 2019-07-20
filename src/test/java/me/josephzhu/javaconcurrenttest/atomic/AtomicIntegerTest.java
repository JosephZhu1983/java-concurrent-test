package me.josephzhu.javaconcurrenttest.atomic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class AtomicIntegerTest {
    @Test
    public void test() throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        List<Thread> threadList = IntStream.range(0, 10).mapToObj(i -> {
            Thread thread = new Thread(() -> {
                log.debug("Wait {}->{}", i, i + 1);
                while (!atomicInteger.compareAndSet(i, i + 1)) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("Done {}->{}", i, i + 1);
            });
            thread.setName(UUID.randomUUID().toString());
            return thread;
        }).sorted(Comparator.comparing(Thread::getName)).collect(Collectors.toList());

        for (Thread thread : threadList) {
            thread.start();
        }
        for (Thread thread : threadList) {
            thread.join();
        }
        log.info("result:{}", atomicInteger.get());
    }
}
