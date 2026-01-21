package ru.job4j.concurrent;

public class ThreadState {
    public static void main(String[] args) {
        Thread first = new Thread(
                () -> {
                }
        );

        System.out.println(first.getName());

        Thread second = new Thread(
                () -> {
                }
        );

        System.out.println(second.getName());

        first.start();
        second.start();

        while (first.getState() != Thread.State.TERMINATED & second.getState() != Thread.State.TERMINATED) {
            System.out.println("Дожидаемся выполнения нитей");
        }

        System.out.println("Работа завершена!");
    }
}
