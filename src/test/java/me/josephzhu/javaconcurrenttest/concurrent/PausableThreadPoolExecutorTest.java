package me.josephzhu.javaconcurrenttest.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
public class PausableThreadPoolExecutorTest {

    @Test
    public void test() throws InterruptedException {
        PausableThreadPoolExecutor threadPool = new PausableThreadPoolExecutor(1,1,0,TimeUnit.HOURS,new LinkedBlockingQueue<>());
        IntStream.rangeClosed(1, 5).forEach(i->threadPool.submit(()->{
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("I'm done : {}", i);
        }));
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(threadPool::pause, 2, TimeUnit.SECONDS);
        scheduler.schedule(threadPool::resume, 4, TimeUnit.SECONDS);

        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.HOURS);
    }
}
