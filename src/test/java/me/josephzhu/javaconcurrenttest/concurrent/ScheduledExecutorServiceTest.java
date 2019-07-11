package me.josephzhu.javaconcurrenttest.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ScheduledExecutorServiceTest {
    @Test
    public void test1() throws InterruptedException {
        AtomicInteger scheduleAtFixedRateTotal = new AtomicInteger();
        ScheduledExecutorService scheduleAtFixedRateExecutorService = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture scheduleAtFixedRateTotalFuture = scheduleAtFixedRateExecutorService.scheduleAtFixedRate(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("scheduleAtFixedRate:" + scheduleAtFixedRateTotal.incrementAndGet());
        }, 0, 100, TimeUnit.MILLISECONDS);
        scheduleAtFixedRateExecutorService.schedule(() -> scheduleAtFixedRateTotalFuture.cancel(false), 1, TimeUnit.SECONDS);
        while (!scheduleAtFixedRateTotalFuture.isDone()) TimeUnit.MILLISECONDS.sleep(1);
        Assert.assertEquals(11, scheduleAtFixedRateTotal.get());

        AtomicInteger scheduleWithFixedDelayTotal = new AtomicInteger();
        ScheduledExecutorService scheduleWithFixedDelayExecutorService = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture scheduleWithFixedDelayFuture = scheduleWithFixedDelayExecutorService.scheduleWithFixedDelay(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("scheduleWithFixedDelay:" + scheduleWithFixedDelayTotal.incrementAndGet());
        }, 0, 100, TimeUnit.MILLISECONDS);
        scheduleWithFixedDelayExecutorService.schedule(() -> scheduleWithFixedDelayFuture.cancel(false), 1, TimeUnit.SECONDS);
        while (!scheduleWithFixedDelayFuture.isDone()) TimeUnit.MILLISECONDS.sleep(1);
        Assert.assertEquals(5, scheduleWithFixedDelayTotal.get());
    }
}
