package me.josephzhu.javaconcurrenttest.atomic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class AtomicReferenceTest {

    private Switch rawValue = new Switch();
    private volatile Switch volatileValue = new Switch();
    private AtomicReference<Switch> atomicValue = new AtomicReference<>(new Switch());

    @Test
    public void test() throws InterruptedException {

        new Thread(() -> {
            log.info("Start:rawValue");
            while (rawValue.get()) {
            }
            log.info("Done:rawValue");
        }).start();

        new Thread(() -> {
            log.info("Start:volatileValue");
            while (volatileValue.get()) {
            }
            log.info("Done:volatileValue");
        }).start();

        new Thread(() -> {
            log.info("Start:atomicValue");
            while (atomicValue.get().get()) {
            }
            log.info("Done:atomicValue");
        }).start();

        Executors.newSingleThreadScheduledExecutor().schedule(rawValue::off, 2, TimeUnit.SECONDS);
        Executors.newSingleThreadScheduledExecutor().schedule(volatileValue::off, 2, TimeUnit.SECONDS);
        Executors.newSingleThreadScheduledExecutor().schedule(atomicValue.get()::off, 2, TimeUnit.SECONDS);

        TimeUnit.HOURS.sleep(1);
    }

    class Switch {
        private boolean enable = true;

        public boolean get() {
            return enable;
        }

        public void off() {
            enable = false;
        }
    }
}
