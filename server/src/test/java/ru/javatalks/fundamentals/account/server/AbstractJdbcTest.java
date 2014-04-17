package ru.javatalks.fundamentals.account.server;

import liquibase.exception.LiquibaseException;
import org.junit.BeforeClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AbstractJdbcTest {
    private static final String JDBC_URL = "jdbc:derby:memory:account-service;create=true";

    @BeforeClass
    public static void setUp() throws SQLException, LiquibaseException {
        LiquibaseHelper liquibaseHelper = new LiquibaseHelper("account-service.liquibase.xml", getConnection());
        liquibaseHelper.migrate();
    }

    protected static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }
}
