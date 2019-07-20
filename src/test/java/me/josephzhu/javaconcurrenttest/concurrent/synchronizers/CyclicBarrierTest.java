package me.josephzhu.javaconcurrenttest.concurrent.synchronizers;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CyclicBarrierTest {
    @Test
    public void test() throws InterruptedException {

        int taskCount = 10;
        AtomicInteger atomicInteger = new AtomicInteger(0);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(taskCount);
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < taskCount; i++) {
                final String index = String.format("%d-%d", j, i);
                threadPool.execute(() -> {
                    atomicInteger.incrementAndGet();
                    //log.debug("start:{}", index);
                    try {
                        if (cyclicBarrier.await() == 0) {
                            log.info("done:{}", atomicInteger.getAndSet(0));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        TimeUnit.SECONDS.sleep(30);
    }
}
