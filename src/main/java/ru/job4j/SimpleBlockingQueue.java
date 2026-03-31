package ru.job4j;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.LinkedList;
import java.util.Queue;

@ThreadSafe
public class SimpleBlockingQueue<T> {

    @GuardedBy("this")
    private Queue<T> queue = new LinkedList<>();
    private final int capacity;
    private final Object monitor = this;

    public SimpleBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public void offer(T value) throws InterruptedException {
        synchronized (monitor) {
            while (queue.size() >= capacity) {
                monitor.wait();
            }

            queue.add(value);
            monitor.notifyAll();
        }
    }

    public T poll() throws InterruptedException {
        synchronized (monitor) {
            while (queue.isEmpty()) {
                monitor.wait();
            }

            T result = queue.poll();
            monitor.notifyAll();
            return result;
        }
    }
}
