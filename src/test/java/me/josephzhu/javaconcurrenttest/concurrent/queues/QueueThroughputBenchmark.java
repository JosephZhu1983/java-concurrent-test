package me.josephzhu.javaconcurrenttest.concurrent.queues;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class QueueThroughputBenchmark {

    private final static int element_count = 100000000;

    @Test
    public void test() throws InterruptedException {
        List<TestCase> testCases = new ArrayList<>();
        testCases.add(new TestCase(element_count, Mode.ConcurrentProducerAndConsumer, 1, 1));
        testCases.add(new TestCase(element_count, Mode.ConcurrentProducerAndConsumer, 10, 10));
        testCases.add(new TestCase(element_count, Mode.ConcurrentProducerAndConsumer, 100, 100));
        testCases.add(new TestCase(element_count, Mode.ConcurrentProducerAndConsumer, 1, 100));
        testCases.add(new TestCase(element_count, Mode.ConcurrentProducerAndConsumer, 100, 1));

        testCases.add(new TestCase(element_count, Mode.ProducerAndConsumerShareThread, 1, 0));
        testCases.add(new TestCase(element_count, Mode.ProducerAndConsumerShareThread, 10, 0));
        testCases.add(new TestCase(element_count, Mode.ProducerAndConsumerShareThread, 100, 0));

        testCases.add(new TestCase(element_count, Mode.ProducerAndThenConsumer, 1, 1));
        testCases.add(new TestCase(element_count, Mode.ProducerAndThenConsumer, 10, 10));
        testCases.add(new TestCase(element_count, Mode.ProducerAndThenConsumer, 100, 100));

        LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<>();
        for (TestCase testCase : testCases) {
            System.gc();
            benchmark(linkedBlockingQueue, testCase);
        }
        linkedBlockingQueue = null;

        LinkedTransferQueue<String> linkedTransferQueue = new LinkedTransferQueue<>();
        for (TestCase testCase : testCases) {
            System.gc();
            benchmark(linkedTransferQueue, testCase);
        }
        linkedTransferQueue = null;

        ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<>(element_count);
        for (TestCase testCase : testCases) {
            System.gc();
            benchmark(arrayBlockingQueue, testCase);
        }
        arrayBlockingQueue = null;
    }

    private void benchmark(BlockingQueue<String> queue, TestCase testCase) throws InterruptedException {

        long begin = System.currentTimeMillis();
        log.info("\r\n==========================\r\nBegin benchmark Queue:[{}], case:{}", queue.getClass().getSimpleName(),
                testCase.toString());
        CountDownLatch startCountDownLatch = new CountDownLatch(1);

        if (testCase.mode == Mode.ProducerAndConsumerShareThread) {
            CountDownLatch finishCountDownLatch = new CountDownLatch(testCase.getProducerCount());
            for (int i = 0; i < testCase.getProducerCount(); i++) {
                new Thread(new ProducerTask(
                        startCountDownLatch,
                        finishCountDownLatch,
                        String.format("Thread_%d_", i),
                        queue,
                        testCase)).start();
            }
            startCountDownLatch.countDown();
            finishCountDownLatch.await();

        } else if (testCase.mode == Mode.ConcurrentProducerAndConsumer) {
            CountDownLatch finishCountDownLatch = new CountDownLatch(testCase.getProducerCount() + testCase.getConsumerCount());
            for (int i = 0; i < testCase.getProducerCount(); i++) {
                new Thread(new ProducerTask(
                        startCountDownLatch,
                        finishCountDownLatch,
                        String.format("Thread_%d_", i),
                        queue,
                        testCase)).start();
            }
            for (int i = 0; i < testCase.getConsumerCount(); i++) {
                new Thread(new ConsumerTask(
                        startCountDownLatch,
                        finishCountDownLatch,
                        queue,
                        testCase)).start();
            }
            startCountDownLatch.countDown();
            finishCountDownLatch.await();
        } else if (testCase.mode == Mode.ProducerAndThenConsumer) {
            CountDownLatch finishCountDownLatch = new CountDownLatch(testCase.getProducerCount());
            for (int i = 0; i < testCase.getProducerCount(); i++) {
                new Thread(new ProducerTask(
                        startCountDownLatch,
                        finishCountDownLatch,
                        String.format("Thread_%d_", i),
                        queue,
                        testCase)).start();
            }
            startCountDownLatch.countDown();
            finishCountDownLatch.await();

            startCountDownLatch = new CountDownLatch(1);
            finishCountDownLatch = new CountDownLatch(testCase.getConsumerCount());
            for (int i = 0; i < testCase.getConsumerCount(); i++) {
                new Thread(new ConsumerTask(
                        startCountDownLatch,
                        finishCountDownLatch,
                        queue,
                        testCase)).start();
            }
            startCountDownLatch.countDown();
            finishCountDownLatch.await();
        }

        long finish = System.currentTimeMillis();
        log.info("Finish benchmark Queue:[{}], case:{}, QPS:{}\r\n==========================\n", queue.getClass().getSimpleName(),
                testCase.toString(),
                (long) element_count * 1000 / (finish - begin));
    }

    enum Mode {
        ProducerAndConsumerShareThread,
        ProducerAndThenConsumer,
        ConcurrentProducerAndConsumer
    }

    class ProducerTask implements Runnable {

        private String name;
        private BlockingQueue<String> queue;
        private TestCase testCase;
        private CountDownLatch startCountDownLatch;
        private CountDownLatch finishCountDownLatch;

        public ProducerTask(CountDownLatch startCountDownLatch,
                            CountDownLatch finishCountDownLatch,
                            String name,
                            BlockingQueue<String> queue,
                            TestCase testCase) {
            this.startCountDownLatch = startCountDownLatch;
            this.finishCountDownLatch = finishCountDownLatch;
            this.name = name;
            this.queue = queue;
            this.testCase = testCase;
        }

        @Override
        public void run() {

            try {
                startCountDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int count = testCase.elementCount / testCase.getProducerCount();

            if (testCase.mode == Mode.ProducerAndConsumerShareThread) {
                for (int i = 0; i < count; i++) {
                    try {
                        queue.put(name + i);
                        queue.take();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                for (int i = 0; i < count; i++) {
                    try {
                        queue.put(name + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            finishCountDownLatch.countDown();
        }
    }

    class ConsumerTask implements Runnable {

        private BlockingQueue<String> queue;
        private TestCase testCase;
        private CountDownLatch startCountDownLatch;
        private CountDownLatch finishCountDownLatch;

        public ConsumerTask(CountDownLatch startCountDownLatch,
                            CountDownLatch finishCountDownLatch,
                            BlockingQueue<String> queue,
                            TestCase testCase) {
            this.startCountDownLatch = startCountDownLatch;
            this.finishCountDownLatch = finishCountDownLatch;
            this.queue = queue;
            this.testCase = testCase;
        }

        @Override
        public void run() {
            try {
                startCountDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int count = testCase.elementCount / testCase.getConsumerCount();

            if (testCase.mode != Mode.ProducerAndConsumerShareThread) {
                for (int i = 0; i < count; i++) {
                    try {
                        queue.take();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            finishCountDownLatch.countDown();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class TestCase {
        private int elementCount;
        private Mode mode;
        private int producerCount;
        private int consumerCount;
    }
}
