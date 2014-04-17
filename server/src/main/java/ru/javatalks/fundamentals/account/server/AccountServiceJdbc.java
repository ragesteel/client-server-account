package ru.javatalks.fundamentals.account.server;

import lombok.extern.java.Log;
import ru.javatalks.fundamentals.account.AccountService;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

@Log
public class AccountServiceJdbc implements AccountService {
    private final DataSource dataSource;

    public AccountServiceJdbc(@Nonnull DataSource dataSource) {
        this.dataSource = requireNonNull(dataSource);
    }

    @CheckReturnValue
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
            // TODO Сделать повтор при ошибке коммита.
            try (PreparedStatement preparedStatement =
                         connection.prepareStatement("UPDATE TABLE account SET value = value + ? WHERE id = ?")) {
                preparedStatement.setLong(1, value);
                preparedStatement.setInt(2, id);
                int updatedCount = preparedStatement.executeUpdate();
                if (1 == updatedCount) {
                    return;
                }
            }
            try (PreparedStatement preparedStatement =
                         connection.prepareStatement("INSERT INTO TABLE account (id, value) VALUES (?, ?)")) {
                preparedStatement.setInt(1, id);
                preparedStatement.setLong(2 , value);
                int updatedCount = preparedStatement.executeUpdate();
                if (1 != updatedCount) {
                    log.log(INFO, "Updated count from INSERT is not one, got {0} instead", updatedCount);
                }
            }
            connection.commit();
        } catch (SQLException e) {
            log.log(WARNING, "Got exception", e);
            throw new RuntimeException("Error processing request", e);
        }
    }
}
