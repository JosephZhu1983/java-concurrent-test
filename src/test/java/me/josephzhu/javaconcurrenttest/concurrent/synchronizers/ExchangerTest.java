package me.josephzhu.javaconcurrenttest.concurrent.synchronizers;

import lombok.extern.slf4j.Slf4j;
import me.josephzhu.javaconcurrenttest.concurrent.executors.ThreadFactoryImpl;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ExchangerTest {

    @Test
    public void test() throws InterruptedException {
        Random random = new Random();
        Exchanger<Integer> exchanger = new Exchanger<>();
        int count = 10;
        Executors.newFixedThreadPool(1, new ThreadFactoryImpl("producer"))
                .execute(() -> {
                    try {
                        for (int i = 0; i < count; i++) {
                            log.info("sent:{}", i);
                            exchanger.exchange(i);
                            TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

        ExecutorService executorService = Executors.newFixedThreadPool(1, new ThreadFactoryImpl("consumer"));
        executorService.execute(() -> {
            try {
                for (int i = 0; i < count; i++) {
                    int data = exchanger.exchange(null);
                    log.info("got:{}", data);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }
}
