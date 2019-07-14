package me.josephzhu.javaconcurrenttest.concurrent.queues;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;

@Slf4j
public abstract class Worker implements Runnable {
    protected volatile boolean enable = true;
    protected String name;
    protected BlockingQueue<Integer> queue;

    public Worker(String name, BlockingQueue<Integer> queue) {
        this.name = name;
        this.queue = queue;
    }

    public void stop() {
        this.enable = false;
        log.info("Stop:{}", name);
    }
}