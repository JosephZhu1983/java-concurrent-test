package me.josephzhu.javaconcurrenttest.atomic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

@Slf4j
public class AtomicStampedReferenceTest {
    @Test
    public void test() throws InterruptedException {

        AtomicInteger atomicInteger = new AtomicInteger(1);
        Thread thread1 = new Thread(() -> {
            int value = atomicInteger.get();
            log.info("thread 1 read value: " + value);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (atomicInteger.compareAndSet(value, 3)) {
                log.info("thread 1 update from " + value + " to 3");
            } else {
                log.info("thread 1 update fail!");
            }
        });
        thread1.start();

        Thread thread2 = new Thread(() -> {
            int value = atomicInteger.get();
            log.info("thread 2 read value: " + value);
            if (atomicInteger.compareAndSet(value, 2)) {
                log.info("thread 2 update from " + value + " to 2");

                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                value = atomicInteger.get();
                log.info("thread 2 read value: " + value);
                if (atomicInteger.compareAndSet(value, 1)) {
                    log.info("thread 2 update from " + value + " to 1");
                }
            }
        });
        thread2.start();

        thread1.join();
        thread2.join();
    }

    @Test
    public void test2() throws InterruptedException {
        AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(1, 1);

        Thread thread1 = new Thread(() -> {
            int[] stampHolder = new int[1];
            int value = atomicStampedReference.get(stampHolder);
            int stamp = stampHolder[0];
            log.info("thread 1 read value: " + value + ", stamp: " + stamp);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (atomicStampedReference.compareAndSet(value, 3, stamp, stamp + 1)) {
                log.info("thread 1 update from " + value + " to 3");
            } else {
                log.info("thread 1 update fail!");
            }
        });
        thread1.start();

        Thread thread2 = new Thread(() -> {
            int[] stampHolder = new int[1];
            int value = atomicStampedReference.get(stampHolder);
            int stamp = stampHolder[0];
            log.info("thread 2 read value: " + value + ", stamp: " + stamp);
            if (atomicStampedReference.compareAndSet(value, 2, stamp, stamp + 1)) {
                log.info("thread 2 update from " + value + " to 2");

                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                value = atomicStampedReference.get(stampHolder);
                stamp = stampHolder[0];
                log.info("thread 2 read value: " + value + ", stamp: " + stamp);
                if (atomicStampedReference.compareAndSet(value, 1, stamp, stamp + 1)) {
                    log.info("thread 2 update from " + value + " to 1");
                }
                value = atomicStampedReference.get(stampHolder);
                stamp = stampHolder[0];
                log.info("thread 2 read value: " + value + ", stamp: " + stamp);
            }
        });
        thread2.start();

        thread1.join();
        thread2.join();
    }
}
