package me.josephzhu.javaconcurrenttest.concurrent.executors;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
public class TomcatThreadPoolTest {

    @Test
    public void test() throws InterruptedException {
        TomcatTaskQueue taskqueue = new TomcatTaskQueue(10);
        TomcatThreadPool threadPool = new TomcatThreadPool(2, 5, 0, TimeUnit.HOURS, taskqueue);
        taskqueue.setParent(threadPool);
        IntStream.rangeClosed(1, 10).forEach(i -> threadPool.execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("Slow task I'm done : {}", i);
        }));

        IntStream.rangeClosed(1, 10).forEach(i -> threadPool.execute(() -> {
            log.info("Quick task I'm done : {}", i);
        }, 1010, TimeUnit.MILLISECONDS));

        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.HOURS);
    }
}
