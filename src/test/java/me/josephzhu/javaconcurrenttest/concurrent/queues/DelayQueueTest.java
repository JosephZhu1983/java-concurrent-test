package me.josephzhu.javaconcurrenttest.concurrent.queues;


import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
public class DelayQueueTest {

    @Test
    public void test() throws InterruptedException {
        DelayQueue<Message> delayQueue = new DelayQueue<>();
        IntStream.rangeClosed(1, 10).forEach(i -> {
            for (int __ = 0; __ < 2; __++)
                delayQueue.add(new Message(i * 1000));
        });

        Executors.newFixedThreadPool(1).submit(() -> {
            while (true) {
                Message message = delayQueue.take();
                log.debug("Got:{}", message);
            }
        });

        TimeUnit.SECONDS.sleep(20);
    }


    @ToString
    class Message implements Delayed {

        private final long delay;
        private final long expire;

        public Message(long delay) {
            this.delay = delay;
            expire = System.currentTimeMillis() + delay;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            //log.debug("getDelay called : {}", unit);
            return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        }
    }
}
