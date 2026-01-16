package ru.job4j.concurrent;

public class ThreadState {
    public static void main(String[] args) {
        Thread first = new Thread(
                () -> {
                }
        );
        System.out.println(first.getName() + " " + first.getState());
        first.start();
        while (first.getState() != Thread.State.TERMINATED) {
            System.out.println(first.getState());
        }
        System.out.println(first.getName() + " " + first.getState());

        Thread second = new Thread(
                () -> {
                }
        );
        System.out.println(second.getName() + " " + second.getState());
        second.start();
        while (second.getState() != Thread.State.TERMINATED) {
            System.out.println(second.getState());
        }
        System.out.println(second.getName() + " " + second.getState());

        System.out.println("Работа завершена!");
    }
}
