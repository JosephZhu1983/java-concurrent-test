package me.josephzhu.javaconcurrenttest.lock;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class ReentrantLockTest {

    @Test
    public void test2() {
        ReentrantLock reentrantLock = new ReentrantLock(true);
        int i = 0;
        try {
            while (true) {
                reentrantLock.lock();
                i++;
            }
        } catch (Error error) {
            log.error("count:{}", i, error);
        }
    }

    @Test
    public void test() throws InterruptedException {

        ReentrantLock reentrantLock = new ReentrantLock(true);
        IntStream.rangeClosed(1, 10).forEach(i -> reentrantLock.lock());
        log.info("getHoldCount:{},isHeldByCurrentThread:{},isLocked:{}",
                reentrantLock.getHoldCount(),
                reentrantLock.isHeldByCurrentThread(),
                reentrantLock.isLocked());

        List<Thread> threads = IntStream.rangeClosed(1, 10).mapToObj(i -> new Thread(() -> {
            try {
                if (reentrantLock.tryLock(i, TimeUnit.SECONDS)) {
                    try {
                        log.debug("Got lock");
                    } finally {
                        reentrantLock.unlock();
                    }
                } else {
                    log.debug("Cannot get lock");
                }
            } catch (InterruptedException e) {
                log.debug("InterruptedException Cannot get lock");
                e.printStackTrace();
            }
        })).collect(Collectors.toList());

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> log.info("getHoldCount:{}, getQueueLength:{}, hasQueuedThreads:{}, waitThreads:{}",
                reentrantLock.getHoldCount(),
                reentrantLock.getQueueLength(),
                reentrantLock.hasQueuedThreads(),
                threads.stream().filter(reentrantLock::hasQueuedThread).count()), 0, 1, TimeUnit.SECONDS);

        threads.forEach(Thread::start);

        TimeUnit.SECONDS.sleep(5);
        IntStream.rangeClosed(1, 10).forEach(i -> reentrantLock.unlock());
        TimeUnit.SECONDS.sleep(1);
    }
}
