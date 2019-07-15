package me.josephzhu.javaconcurrenttest.concurrent.queues;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Consumer2 extends Worker {

    private static AtomicInteger totalConsumedAfterShutdown = new AtomicInteger();

    public Consumer2(String name, BlockingQueue<Integer> queue) {
        super(name, queue);
    }

    public static int totalConsumedAfterShutdown() {
        return totalConsumedAfterShutdown.get();
    }

    @Override
    public void run() {
        while (enable || queue.size() > 0) {
            try {
                Integer item = queue.poll(1, TimeUnit.SECONDS);
                log.info("size:{}, got:{}, enable:{}", queue.size(), item, enable);
                if (!enable && item != null) {
                    totalConsumedAfterShutdown.incrementAndGet();
                }
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
            }
        }
        log.info("{} quit", name);
    }
}
