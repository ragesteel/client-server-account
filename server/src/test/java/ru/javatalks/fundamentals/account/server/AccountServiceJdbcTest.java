package ru.javatalks.fundamentals.account.server;

import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class AccountServiceJdbcTest extends AbstractJdbcTest {

    @Test
    public void testInitialZero() throws SQLException {
        Connection connection = getConnection();
        long actualAmount = AccountServiceJdbc.getAmount(connection, 1);
        assertEquals(0, actualAmount);
    }

    @Test
    public void testCreation() throws SQLException {
        Connection connection = getConnection();
        AccountServiceJdbc.addAmount(connection, 2, 100L);
        long actualAmount = AccountServiceJdbc.getAmount(connection, 2);
        assertEquals(100, actualAmount);
    }

    @Test
    public void testUpdateExisting() throws SQLException {
        Connection connection = getConnection();
        AccountServiceJdbc.addAmount(connection, 3, 100L);
        AccountServiceJdbc.addAmount(connection, 3, 100L);
        long actualAmount = AccountServiceJdbc.getAmount(connection, 3);
        assertEquals(200, actualAmount);
    }

}
