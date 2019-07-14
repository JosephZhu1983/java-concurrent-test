package me.josephzhu.javaconcurrenttest.concurrent.queues;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Producer extends Worker {
    private static AtomicInteger atomicInteger = new AtomicInteger();

    public Producer(String name, BlockingQueue<Integer> queue) {
        super(name, queue);
    }

    @Override
    public void run() {
        while (enable) {
            try {
                queue.put(atomicInteger.incrementAndGet());
                log.info("size:{}, put:{}, enable:{}", queue.size(), atomicInteger.get(), enable);
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("{} quit", name);
    }
}
