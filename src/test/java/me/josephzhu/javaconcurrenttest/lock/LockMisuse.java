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
        IntStream.rangeClosed(1, 100000).forEach(i -> executorService.submit(new Container()::test2));
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
        log.info("{}", Container.counter1);
    }

    @Test
    public void test2() throws InterruptedException {
        log.debug("begin");
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        IntStream.rangeClosed(1, 10).forEach(i -> executorService.submit(new Container()::test1));
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
        log.debug("end");
    }

}

class Container {
    static Object locker1 = new Object();
    static int counter1 = 0;
    Object locker2 = new Object();
    int counter2 = 0;

    void test1() {
        synchronized (locker1) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter2++;
        }
    }

    void test2() {
        synchronized (locker2) {
            counter1++;
        }
    }
}
