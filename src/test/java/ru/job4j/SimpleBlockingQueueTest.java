package ru.job4j;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

class SimpleBlockingQueueTest {

    @Test
    void whenProducerAndConsumerWorkThenQueuePassesElements() throws InterruptedException {
        SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(5);
        List<Integer> produced = new ArrayList<>();
        List<Integer> consumed = new ArrayList<>();

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    queue.offer(i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                produced.add(i);
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    Integer val = queue.poll();
                    if (val != null) {
                        consumed.add(val);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        assertEquals(5, produced.size());
        assertEquals(5, consumed.size());
        assertEquals(produced, consumed);
    }

    @Test
    void whenQueueIsFullThenProducerWaits() throws InterruptedException {
        SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(2);
        List<String> log = new ArrayList<>();

        Thread producer = new Thread(() -> {
            try {
                queue.offer(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.add("Producer: added 1");
            try {
                queue.offer(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.add("Producer: added 2");

            log.add("Producer: trying to add 3 (should wait)");
            try {
                queue.offer(3); // Здесь должен заблокироваться, пока потребитель не заберет
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.add("Producer: added 3");

        });

        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.add("Consumer: waking up and taking element");
            try {
                queue.poll();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        assertTrue(log.contains("Producer: trying to add 3 (should wait)"));
        assertTrue(log.contains("Producer: added 3"));
        assertTrue(log.indexOf("Producer: added 3") > log.indexOf("Consumer: waking up and taking element"));
    }

    @Test
    void whenQueueIsEmptyThenConsumerWaits() throws InterruptedException {
        SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(5);
        List<String> log = new ArrayList<>();

        Thread consumer = new Thread(() -> {
            log.add("Consumer: start (should wait)");
            try {
                Integer val = queue.poll();
                log.add("Consumer: received " + val);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread producer = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.add("Producer: adding element");
            try {
                queue.offer(42);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        consumer.start();
        producer.start();

        producer.join();
        consumer.join();

        assertTrue(log.contains("Consumer: start (should wait)"));
        assertTrue(log.contains("Consumer: received 42"));
        assertTrue(log.indexOf("Consumer: received 42") > log.indexOf("Producer: adding element"));
    }

    @Test
    public void whenFetchAllThenGetIt() throws InterruptedException {
        final CopyOnWriteArrayList<Integer> buffer = new CopyOnWriteArrayList<>();
        final SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(5);
        Thread producer = new Thread(
                () -> {
                    IntStream.range(0, 5).forEach(i -> {
                        try {
                            queue.offer(i);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
                }
        );
        producer.start();

        Thread consumer = new Thread(
                () -> {
                    while (!queue.isEmpty() || !Thread.currentThread().isInterrupted()) {
                        try {
                            buffer.add(queue.poll());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        }
                    }
                }
        );
        consumer.start();

        producer.join();
        consumer.interrupt();
        consumer.join();
        assertThat(buffer).containsExactly(0, 1, 2, 3, 4);
    }
}