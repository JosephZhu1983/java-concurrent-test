package me.josephzhu.javaconcurrenttest.concurrent.queues;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ArrayBlockingQueueTest {

    @Test
    public void test() throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(100, false);
        List<Worker> workers = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String name = "Producer" + i;
            Producer worker = new Producer(name, queue);
            workers.add(worker);
            Thread thread = new Thread(worker);
            thread.setName(name);
            threads.add(thread);
            thread.start();
        }
        for (int i = 0; i < 4; i++) {
            String name = "Consumer" + i;
            Consumer worker = new Consumer(name, queue);
            workers.add(worker);
            Thread thread = new Thread(worker);
            thread.setName(name);
            threads.add(thread);
            thread.start();
        }

        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            for (Worker worker : workers) {
                worker.stop();
            }
        }, 2, TimeUnit.SECONDS);

        for (Thread thread : threads) {
            thread.join();
        }
        log.info("totalConsumedAfterShutdown:{}", Consumer.totalConsumedAfterShutdown());
    }
}
