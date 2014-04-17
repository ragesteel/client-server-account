package ru.javatalks.fundamentals.account.server;

import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;

public class AccountServiceJdbcConcurrentTest extends AbstractJdbcTest {
    private static final int THREAD_COUNT = 100;
    private static final int ACCOUNT_ID = 4;
    private static final long AMOUNT = 100L;

    @Test
    public void testConcurrentAdd() throws InterruptedException, SQLException {
        final CountDownLatch startLatch = new CountDownLatch(THREAD_COUNT);
        final CountDownLatch finishLatch = new CountDownLatch(THREAD_COUNT);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    startLatch.countDown();
                    startLatch.await();
                    addAmount();
                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finishLatch.countDown();
                }
            }
        };
        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(runnable).start();
        }
        finishLatch.await();

        long actualAmount = AccountServiceJdbc.getAmount(getConnection(), ACCOUNT_ID);
        assertEquals(THREAD_COUNT * AMOUNT, actualAmount);
    }

    private static void addAmount() throws SQLException, InterruptedException {
        Connection connection = getConnection();
        connection.setAutoCommit(false);
        AccountServiceJdbc.addAmountCycle(connection, ACCOUNT_ID, AMOUNT);
        MILLISECONDS.sleep((long)(Math.random() * 100));
        connection.commit();
    }
}
