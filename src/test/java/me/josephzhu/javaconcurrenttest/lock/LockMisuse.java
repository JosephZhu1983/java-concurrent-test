package me.josephzhu.javaconcurrenttest.lock;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
public class LockMisuse {

    @Test
    public void test1() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        IntStream.rangeClosed(1, 100000).forEach(i -> executorService.submit(new Container()::test));
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
        log.info("{}", Container.counter);
    }
}

class Container {
    static volatile int counter = 0;
    static Object locker = new Object();

    void test() {
        synchronized (locker) {
            counter++;
        }
    }
}
