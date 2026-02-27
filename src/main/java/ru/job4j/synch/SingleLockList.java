package ru.job4j.synch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SingleLockList<T> implements Iterable<T> {
    private final List<T> list;

    public SingleLockList(List<T> list) {
        this.list = copy(list);
    }

    public synchronized void add(T value) {
        list.add(value);
    }

    public synchronized T get(int index) {
        return list.get(index);
    }

    @Override
    public Iterator<T> iterator() {
        return getSnapshot().iterator();
    }

    private synchronized List<T> getSnapshot() {
        return new ArrayList<>(list);
    }

    private synchronized List<T> copy(List<T> origin) {
        List<T> newlist = new ArrayList<>();
        newlist.addAll(origin);
        return newlist;
    }
}
