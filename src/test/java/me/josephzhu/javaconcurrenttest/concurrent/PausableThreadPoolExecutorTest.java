package me.josephzhu.javaconcurrenttest.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

@Slf4j
public class PausableThreadPoolExecutorTest {

    class PausableThreadPoolExecutor extends ThreadPoolExecutor {
        private boolean isPaused;
        private ReentrantLock pauseLock = new ReentrantLock();
        private Condition unpaused = pauseLock.newCondition();

        public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            pauseLock.lock();
            try {
                while (isPaused) unpaused.await();
            } catch (InterruptedException ie) {
                t.interrupt();
            } finally {
                pauseLock.unlock();
            }
        }

        public void pause() {
            pauseLock.lock();
            try {
                isPaused = true;
            } finally {
                pauseLock.unlock();
            }
        }

        public void resume() {
            pauseLock.lock();
            try {
                isPaused = false;
                unpaused.signalAll();
            } finally {
                pauseLock.unlock();
            }
        }
    }

    @Test
    public void test() throws InterruptedException {
        PausableThreadPoolExecutor threadPool = new PausableThreadPoolExecutor(1,1,0,TimeUnit.HOURS,new LinkedBlockingQueue<>());
        IntStream.rangeClosed(1, 5).forEach(i->threadPool.submit(()->{
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("I'm done : {}", i);
        }));
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1) ;
        scheduler.schedule(threadPool::pause, 2, TimeUnit.SECONDS);
        scheduler.schedule(threadPool::resume, 4, TimeUnit.SECONDS);

        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.HOURS);
    }
}
