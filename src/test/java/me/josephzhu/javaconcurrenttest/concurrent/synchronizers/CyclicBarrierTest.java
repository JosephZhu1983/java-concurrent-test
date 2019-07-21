package me.josephzhu.javaconcurrenttest.concurrent.synchronizers;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class CyclicBarrierTest {
    @Test
    public void test() throws InterruptedException {

        int playerCount = 5;
        int playCount = 3;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(playerCount);
        List<Thread> threads = IntStream.rangeClosed(1, playerCount).mapToObj(player -> new Thread(() -> IntStream.rangeClosed(1, playCount).forEach(play -> {
            try {
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(100));
                log.debug("Player {} arrived for play {}", player, play);
                if (cyclicBarrier.await() == 0) {
                    log.info("Total players {} arrived, let's play {}", cyclicBarrier.getParties(), play);
                    TimeUnit.SECONDS.sleep(2);
                    log.info("Play {} finished", play);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }))).collect(Collectors.toList());

        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
