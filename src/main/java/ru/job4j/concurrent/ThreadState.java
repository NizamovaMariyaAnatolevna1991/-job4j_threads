package ru.job4j.concurrent;

public class ThreadState {
    public static void main(String[] args) {
        Thread[] threads = {
                new Thread(() -> {
                }),
                new Thread(() -> {
                })
        };

        for (Thread thread : threads) {
            System.out.println(thread.getName() + " " + thread.getState());
            thread.start();
            while (thread.getState() != Thread.State.TERMINATED) {
                System.out.println(thread.getState());
            }
            System.out.println(thread.getName() + " " + thread.getState());
        }

        System.out.println("Работа завершена!");
    }
}
