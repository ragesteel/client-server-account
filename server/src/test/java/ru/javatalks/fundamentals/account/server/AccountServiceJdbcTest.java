package ru.javatalks.fundamentals.account.server;

import liquibase.exception.LiquibaseException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class AccountServiceJdbcTest {
    private static final String JDBC_URL = "jdbc:derby:memory:account-service;create=true";

    @BeforeClass
    public static void setUp() throws SQLException, LiquibaseException {
        LiquibaseHelper liquibaseHelper = new LiquibaseHelper("account-service.liquibase.xml", getConnection());
        liquibaseHelper.migrate();
    }

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

    @Test
    public void testConcurrentAdd() {

    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }
}
