package me.josephzhu.javaconcurrenttest.atomic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class InterestingProblem {

    int a = 1;
    int b = 1;

    void add() {
        a++;
        b++;
    }

    void compare() {
        if (a < b)
            log.info("a:{},b:{},{}", a, b, a > b);
    }

    @Test
    public void test() throws InterruptedException {

        new Thread(() -> {
            while (true)
                add();
        }).start();
        new Thread(() -> {
            while (true)
                compare();
        }).start();

        TimeUnit.MILLISECONDS.sleep(100);
    }
}
