package me.josephzhu.javaconcurrenttest.concurrent.synchronizers;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Slf4j
public class CountDownLatchTest {
    @Test
    public void test() throws InterruptedException {
        int taskCount = 10;
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
        ExecutorService threadPool = Executors.newCachedThreadPool();
        List<Future<Integer>> tasks = new ArrayList<>();
        LocalDateTime begin = LocalDateTime.now();
        IntStream.rangeClosed(1, taskCount).forEach(i -> tasks.add(threadPool.submit(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(i * 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
            return i;
        })));
        countDownLatch.await();
        log.info("count:{},total:{},took:{}ms", tasks.size(), tasks.stream().map(item -> {
            try {
                return item.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }).mapToInt(Integer::intValue).sum(), Duration.between(begin, LocalDateTime.now()).toMillis());
    }
}
