package ru.javatalks.fundamentals.account.server;

import lombok.RequiredArgsConstructor;
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
    public void testWithSleep() throws InterruptedException, SQLException {
        final CountDownLatch startLatch = new CountDownLatch(THREAD_COUNT);
        final CountDownLatch finishLatch = new CountDownLatch(THREAD_COUNT);

        assertTask(new AbstractTask(startLatch, finishLatch) {
            @Override
            protected void runTask() throws SQLException, InterruptedException {
                    addAmount();
            }
        }, finishLatch);
    }

    @Test
    public void testWithoutSleep() throws InterruptedException, SQLException {
        final CountDownLatch startLatch = new CountDownLatch(THREAD_COUNT);
        final CountDownLatch finishLatch = new CountDownLatch(THREAD_COUNT);

        assertTask(new AbstractTask(startLatch, finishLatch) {
            @Override
            protected void runTask() throws SQLException, InterruptedException {
                AccountServiceJdbc.addAmount(getConnection(), ACCOUNT_ID, AMOUNT);
            }
        }, finishLatch);
    }

    private void assertTask(AbstractTask addThenSleep, CountDownLatch finishLatch)
            throws InterruptedException, SQLException {
        Runnable runnable = addThenSleep;
        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(runnable).start();
        }
        finishLatch.await();

        long actualAmount = AccountServiceJdbc.getAmount(getConnection(), ACCOUNT_ID);
        assertEquals(THREAD_COUNT * AMOUNT, actualAmount);
    }

    @RequiredArgsConstructor
    private abstract static class AbstractTask implements Runnable {

        private final CountDownLatch startLatch;
        private final CountDownLatch finishLatch;

        @Override
        public void run() {
            try {
                startLatch.countDown();
                startLatch.await();
                runTask();
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                finishLatch.countDown();
            }
        }

        protected abstract void runTask() throws SQLException, InterruptedException;
    }

    private static void addAmount() throws SQLException, InterruptedException {
        Connection connection = getConnection();
        connection.setAutoCommit(false);
        AccountServiceJdbc.addAmountCycle(connection, ACCOUNT_ID, AMOUNT);
        MILLISECONDS.sleep((long)(Math.random() * 100));
        connection.commit();
    }
}
