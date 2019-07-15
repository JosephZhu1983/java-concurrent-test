package me.josephzhu.javaconcurrenttest.concurrent.concurrentcollections;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PutIfAbsentTest {

    @Test
    public void test() {
        ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<>();
        log.info("Start");
        log.info("putIfAbsent:{}", concurrentHashMap.putIfAbsent("test", getValue()));
        log.info("computeIfAbsent:{}", concurrentHashMap.computeIfAbsent("test", k -> getValue()));
        log.info("putIfAbsent again:{}", concurrentHashMap.putIfAbsent("test", getValue()));
        log.info("computeIfAbsent agin:{}", concurrentHashMap.computeIfAbsent("test", k -> getValue()));
    }

    private String getValue() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return UUID.randomUUID().toString();
    }
}
