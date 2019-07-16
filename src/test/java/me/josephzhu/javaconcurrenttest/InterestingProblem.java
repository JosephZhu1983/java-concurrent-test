package me.josephzhu.javaconcurrenttest;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class InterestingProblem {

    volatile int a = 1;
    volatile int b = 1;
    volatile int count = 0;

    void add() {
        a++;
        b++;
    }

    void compare() {
        if (a < b && b < a)
            count++;
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
        System.out.println(count);
    }
}
