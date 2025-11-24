package com.mipt.uriilesnikov.multithreading;

public class Bank {

    public void sendToAccount(BankAccount from, BankAccount to, int amount) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Accounts must not be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        BankAccount first = from.getId() < to.getId() ? from : to;
        BankAccount second = from.getId() < to.getId() ? to : from;

        synchronized (first) {
            synchronized (second) {
                if (from.getBalance() < amount) {
                    throw new IllegalArgumentException("Insufficient funds");
                }
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }

    public void sendToAccountDeadlock(BankAccount from, BankAccount to, int amount) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Accounts must not be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        synchronized (from) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}
            synchronized (to) {
                if (from.getBalance() < amount) {
                    throw new IllegalArgumentException("Insufficient funds");
                }
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }
}
