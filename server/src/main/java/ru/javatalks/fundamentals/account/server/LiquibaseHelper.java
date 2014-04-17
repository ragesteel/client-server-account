package ru.javatalks.fundamentals.account.server;

import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import lombok.extern.java.Log;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.INFO;

@Log
public class LiquibaseHelper {

    @Nonnull
    private final String changeLogFile;

    @Nonnull
    private final JdbcConnection jdbcConnection;

    @Nonnull
    private final String contexts;

    private Liquibase liquibase;

    public LiquibaseHelper(@Nonnull String changeLogFile, @Nonnull Connection connection) {
        this.changeLogFile = requireNonNull(changeLogFile);
        jdbcConnection = new JdbcConnection(requireNonNull(connection));
        contexts = "";
    }

    public void migrate() throws LiquibaseException {
        init();

        List<ChangeSet> changeSets = listUnrunChangeSets();
        if (changeSets.isEmpty()) {
            log.log(INFO, "Database structure is up to date.");
            return;
        }
        log.log(INFO, "Found {0} change set to apply.", changeSets.size());
        validate();
        log.log(INFO, "Preconditions is valid.");
        update();
        log.log(INFO, "Change sets successfully applied.");
    }

    private void init() throws LiquibaseException {
        ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
        liquibase = new Liquibase(changeLogFile, resourceAccessor, database);
    }

    private List<ChangeSet> listUnrunChangeSets() throws LiquibaseException {
        return liquibase.listUnrunChangeSets(contexts);
    }

    private void validate() throws LiquibaseException {
        liquibase.validate();
    }

    private void update() throws LiquibaseException {
        liquibase.update(contexts);
    }
}
