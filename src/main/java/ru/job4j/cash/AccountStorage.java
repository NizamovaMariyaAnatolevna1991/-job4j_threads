package ru.job4j.cash;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.HashMap;
import java.util.Optional;

public class AccountStorage {

    private final HashMap<Integer, Account> accounts = new HashMap<>();

    public synchronized boolean add(Account account) {
        return account != null && (accounts.putIfAbsent(account.id(), account) == null);
    }

    public synchronized boolean update(Account account) {
        return account != null && (accounts.replace(account.id(), account) != null);
    }

    public synchronized void delete(int id) {
        accounts.remove(id);
    }

    public synchronized Optional<Account> getById(int id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public synchronized boolean transfer(int fromId, int toId, int amount) {
        Optional<Account> fromOpt = getById(fromId);
        Optional<Account> toOpt = getById(toId);

        if (fromOpt.isEmpty() || toOpt.isEmpty()) {
            return false;
        }

        Account fromAccount = fromOpt.get();
        if (amount <= 0 || fromAccount.amount() < amount) {
            return false;
        }

        boolean fromUpdated = update(new Account(fromId, fromAccount.amount() - amount));
        boolean toUpdated = update(new Account(toId, toOpt.get().amount() + amount));

        return fromUpdated && toUpdated;
    }
}
