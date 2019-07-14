package me.josephzhu.javaconcurrenttest.concurrent.synchronizers;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class PhaserTest {

    @Test
    public void test() throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger();
        int iterations = 10;
        int tasks = 100;
        runTasks(IntStream.rangeClosed(1, tasks)
                .mapToObj(index -> new Thread(() -> {
                    //log.debug("{}",index);
                    atomicInteger.incrementAndGet();
                }))
                .collect(Collectors.toList()), iterations);
        log.info("tasks:{},iterations:{},result:{}", tasks, iterations, atomicInteger.get());
        Assert.assertEquals(tasks * iterations, atomicInteger.get());
    }

    private void runTasks(List<Runnable> tasks, int iterations) throws InterruptedException {
        Phaser phaser = new Phaser() {
            protected boolean onAdvance(int phase, int registeredParties) {
                return phase >= iterations - 1 || registeredParties == 0;
            }
        };
        phaser.register();

        for (Runnable task : tasks) {
            phaser.register();
            new Thread(() -> {
                do {
                    task.run();
                    phaser.arriveAndAwaitAdvance();
                } while (!phaser.isTerminated());
            }).start();
        }

        while (!phaser.isTerminated()) {
            TimeUnit.MILLISECONDS.sleep(10);
            log.debug("phase:{},registered:{},unarrived:{},arrived:{}",
                    phaser.getPhase(),
                    phaser.getRegisteredParties(),
                    phaser.getUnarrivedParties(),
                    phaser.getArrivedParties());
            phaser.arriveAndAwaitAdvance();
        }

    }
}
