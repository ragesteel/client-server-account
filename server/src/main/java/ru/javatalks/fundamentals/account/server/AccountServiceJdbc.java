package ru.javatalks.fundamentals.account.server;

import com.google.common.annotations.VisibleForTesting;
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
        try (Connection connection = dataSource.getConnection()) {
            return getAmount(connection, id);
        } catch (SQLException e) {
            throw new RuntimeException("Error processing request", e);
        }
    }

    @Override
    public void addAmount(@Nonnull Integer id, @Nonnull Long value) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            addAmount(connection, id, value);
            connection.commit();
        } catch (SQLException e) {
            log.log(WARNING, "Got exception", e);
            throw new RuntimeException("Error processing request", e);
        }
    }

    @VisibleForTesting
    protected static Long getAmount(Connection connection, Integer id) throws SQLException {
        try (PreparedStatement preparedStatement =
                     connection.prepareStatement("SELECT amount FROM account WHERE ? = id")) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("amount");
                }
                return 0L;
            }
        }
    }

    @VisibleForTesting
    protected static void addAmount(Connection connection, Integer id, Long value) throws SQLException {
        // TODO Сделать повтор при ошибке коммита.
        try (PreparedStatement preparedStatement =
                     connection.prepareStatement("UPDATE account SET amount = amount + ? WHERE id = ?")) {
            preparedStatement.setLong(1, value);
            preparedStatement.setInt(2, id);
            int updatedCount = preparedStatement.executeUpdate();
            if (1 == updatedCount) {
                return;
            }
        }
        try (PreparedStatement preparedStatement =
                     connection.prepareStatement("INSERT INTO account (id, amount) VALUES (?, ?)")) {
            preparedStatement.setInt(1, id);
            preparedStatement.setLong(2 , value);
            int updatedCount = preparedStatement.executeUpdate();
            if (1 != updatedCount) {
                log.log(INFO, "Updated count from INSERT is not one, got {0} instead", updatedCount);
            }
        }
    }
}
