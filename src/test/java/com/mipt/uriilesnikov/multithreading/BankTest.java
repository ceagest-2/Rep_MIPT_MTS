package com.mipt.uriilesnikov.multithreading;

import org.junit.jupiter.api.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

public class BankTest {

    private Bank bank;
    private BankAccount accountFirst;
    private BankAccount accountSecond;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        accountFirst = new BankAccount(1, 1000);
        accountSecond = new BankAccount(2, 1000);
    }

    @Test
    public void sendToAccount_concurrentTransfers_noDeadlockAndCorrectBalance() throws InterruptedException {
        int nThreads = 10;
        int transfersPerThread = 100;
        int amount = 10;

        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch = new CountDownLatch(nThreads);

        Runnable task = () -> {
            try {
                for (int i = 0; i < transfersPerThread; i++) {
                    bank.sendToAccount(accountFirst, accountSecond, amount);
                    bank.sendToAccount(accountSecond, accountFirst, amount);
                }
            } finally {
                latch.countDown();
            }
        };

        for (int i = 0; i < nThreads; i++) {
            executor.submit(task);
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        int finalBalance1 = accountFirst.getBalance();
        int finalBalance2 = accountSecond.getBalance();

        assertEquals(1000, finalBalance1);
        assertEquals(1000, finalBalance2);
    }

    @Test
    public void sendToAccount_insufficientFunds_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bank.sendToAccount(accountFirst, accountSecond, 2000)
        );
        assertTrue(exception.getMessage().contains("Insufficient funds"));
    }

    @Test
    public void sendToAccount_nullFrom_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> bank.sendToAccount(null, accountSecond, 100));
    }

    @Test
    public void sendToAccount_nullTo_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> bank.sendToAccount(accountFirst, null, 100));
    }

    @Test
    public void sendToAccount_negativeAmount_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> bank.sendToAccount(accountFirst, accountSecond, -50));
    }

    @Test
    public void sendToAccountDeadlock_causesDeadlock_withinTimeout() throws InterruptedException {
        Thread t1 = new Thread(() -> bank.sendToAccountDeadlock(accountFirst, accountSecond, 100));
        Thread t2 = new Thread(() -> bank.sendToAccountDeadlock(accountSecond, accountFirst, 100));

        t1.start();
        t2.start();

        t1.join(2000);
        t2.join(2000);

        boolean likelyDeadlock = t1.isAlive() && t2.isAlive();

        t1.interrupt();
        t2.interrupt();

        assertTrue(likelyDeadlock, "Deadlock did not occur as expected");
    }
}
