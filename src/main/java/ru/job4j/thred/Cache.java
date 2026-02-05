package ru.job4j.thred;

public class Cache {
    private static Cache cache;

    public static synchronized Cache getInstance() {
        if (cache == null) {
            cache = new Cache();
        }
        return cache;
    }
}
