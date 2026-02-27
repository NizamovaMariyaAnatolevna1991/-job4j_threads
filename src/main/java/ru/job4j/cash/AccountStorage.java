package ru.job4j.cash;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.HashMap;
import java.util.Optional;


public class AccountStorage {

    private final HashMap<Integer, Account> accounts = new HashMap<>();

    public synchronized boolean add(Account account) {
        if ((account == null) || accounts.containsKey(account.id())) {
            return false;
        }
        accounts.put(account.id(), account);
        return true;
    }

    public synchronized boolean update(Account account) {
        if ((account == null) || !accounts.containsKey(account.id())) {
            return false;
        }
        accounts.put(account.id(), account);
        return true;
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

        accounts.put(fromId, new Account(fromId, fromOpt.get().amount() - amount));
        accounts.put(toId, new Account(toId, toOpt.get().amount() + amount));

        return true;
    }
}
