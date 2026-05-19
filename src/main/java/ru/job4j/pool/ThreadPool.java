package ru.job4j.pool;

import ru.job4j.SimpleBlockingQueue;

import java.util.LinkedList;
import java.util.List;

public class ThreadPool {

    private final List<Thread> threads = new LinkedList<>();
    private final SimpleBlockingQueue<Runnable> tasks = new SimpleBlockingQueue<>(2);

    public ThreadPool() {
        int size = Runtime.getRuntime().availableProcessors();

        System.out.println("size " + size);

        for (int i = 0; i < size; i++) {
            Thread woker = new Thread(() -> {
                try {
                    Runnable job = tasks.poll();
                    job.run();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threads.add(woker);
            woker.start();
        }
    }

    public void work(Runnable job) throws InterruptedException {
        tasks.offer(job);
    }

    public void shutdown() {
        for (Thread t : threads) {
            t.interrupt();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
