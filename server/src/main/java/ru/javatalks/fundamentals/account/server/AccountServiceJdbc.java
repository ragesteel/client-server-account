package ru.javatalks.fundamentals.account.server;

import ru.javatalks.fundamentals.account.AccountService;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.util.Objects.requireNonNull;

public class AccountServiceJdbc implements AccountService {
    private final DataSource dataSource;

    public AccountServiceJdbc(@Nonnull DataSource dataSource) {
        this.dataSource = requireNonNull(dataSource);
    }

    @Nonnull
    @Override
    public Long getAmount(@Nonnull Integer id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement("SELECT amount FROM account WHERE ? = id")) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("amount");
                }
                return 0L;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error processing request", e);
        }
    }

    @Override
    public void addAmount(@Nonnull Integer id, @Nonnull Long value) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            // TODO
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error processing request", e);
        }
    }
}
