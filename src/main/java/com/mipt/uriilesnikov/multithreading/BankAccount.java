package com.mipt.uriilesnikov.multithreading;

public class BankAccount {
    private final long id;
    private int balance;

    public BankAccount(long id, int initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public long getId() {
        return id;
    }

    public synchronized int getBalance() {
        return balance;
    }

    public synchronized void withdraw(int amount) {
        balance -= amount;
    }

    public synchronized void deposit(int amount) {
        balance += amount;
    }
}
