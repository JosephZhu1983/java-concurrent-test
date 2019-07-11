package me.josephzhu.javaconcurrenttest.concurrent.synchronizers;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
public class SemaphoreTest {
    @Test
    public void test() throws InterruptedException {
        Semaphore semaphore = new Semaphore(10, true);
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        IntStream.rangeClosed(1, 10000).forEach(i -> {
            threadPool.execute(new Player("Player" + i, semaphore));
        });
        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.HOURS);
        Player.result();
    }
}
