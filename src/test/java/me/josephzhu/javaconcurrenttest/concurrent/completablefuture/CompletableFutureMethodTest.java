package me.josephzhu.javaconcurrenttest.concurrent.completablefuture;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
public class CompletableFutureMethodTest {

    private Supplier<Integer> getInt(int i) {
        return () -> {
            try {
                TimeUnit.SECONDS.sleep(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return i;
        };
    }

    @Test
    public void acceptEither() {
        CompletableFuture.supplyAsync(getInt(2))
                .acceptEither(CompletableFuture.supplyAsync(getInt(3)), System.out::println).join();
    }
}
