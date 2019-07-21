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

    AtomicInteger atomicInteger = new AtomicInteger();

    @Test
    public void test() throws InterruptedException {
        int iterations = 10;
        int tasks = 100;
        runTasks(IntStream.rangeClosed(1, tasks)
                .mapToObj(index -> new Thread(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    atomicInteger.incrementAndGet();
                }))
                .collect(Collectors.toList()), iterations);
        Assert.assertEquals(tasks * iterations, atomicInteger.get());
    }

    private void runTasks(List<Runnable> tasks, int iterations) {
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
            doPostOperation(phaser);
            phaser.arriveAndAwaitAdvance();
        }
        doPostOperation(phaser);
    }

    private void doPostOperation(Phaser phaser) {
        while (phaser.getArrivedParties() < 100) {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("phase:{},registered:{},unarrived:{},arrived:{},result:{}",
                phaser.getPhase(),
                phaser.getRegisteredParties(),
                phaser.getUnarrivedParties(),
                phaser.getArrivedParties(), atomicInteger.get());
    }
}
