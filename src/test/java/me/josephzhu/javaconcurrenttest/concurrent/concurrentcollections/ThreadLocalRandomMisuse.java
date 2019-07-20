package me.josephzhu.javaconcurrenttest.concurrent.concurrentcollections;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
public class ThreadLocalRandomMisuse {
    @Test
    public void test() throws InterruptedException {
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        IntStream.rangeClosed(1, 5)
                .mapToObj(i -> new Thread(() -> log.info("wrong:{}", threadLocalRandom.nextInt())))
                .forEach(Thread::start);
        IntStream.rangeClosed(1, 5)
                .mapToObj(i -> new Thread(() -> log.info("ok:{}", ThreadLocalRandom.current().nextInt())))
                .forEach(Thread::start);
        TimeUnit.SECONDS.sleep(1);
    }
}
