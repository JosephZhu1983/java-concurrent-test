package me.josephzhu.javaconcurrenttest.concurrent.synchronizers;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
public class Player implements Runnable {

    private static AtomicInteger totalPlayer = new AtomicInteger();
    private static AtomicLong longestWait = new AtomicLong();
    private static LongAdder totalWait = new LongAdder();
    private String playerName;
    private Semaphore semaphore;
    private LocalDateTime enterTime;

    public Player(String playerName, Semaphore semaphore) {
        this.playerName = playerName;
        this.semaphore = semaphore;
    }

    public static void result() {
        log.info("totalPlayer:{},longestWait:{}ms,averageWait:{}ms", totalPlayer.get(), longestWait.get(), totalWait.doubleValue() / totalPlayer.get());
    }

    @Override
    public void run() {
        try {
            enterTime = LocalDateTime.now();
            semaphore.acquire();
            totalPlayer.incrementAndGet();
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
            long ms = Duration.between(enterTime, LocalDateTime.now()).toMillis();
            longestWait.accumulateAndGet(ms, Math::max);
            totalWait.add(ms);
            //log.debug("Player:{} finished, took:{}ms", playerName, ms);
        }
    }
}
