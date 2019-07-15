package me.josephzhu.javaconcurrenttest.atomic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class AtomicIntegerTest {
    @Test
    public void test() throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < 1000; i++) {
            final int j = i;
            Thread t = new Thread(() -> {
                if (j % 10 == 0) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (!atomicInteger.compareAndSet(j, j + 1)) {
                }
                log.debug("{}->{}", j, j + 1);
            });
            t.start();
            t.join();
        }
        log.info("result:{}", atomicInteger.get());
    }
}
