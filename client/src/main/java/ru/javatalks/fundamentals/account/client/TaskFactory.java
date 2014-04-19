package ru.javatalks.fundamentals.account.client;

import javax.annotation.Nonnull;

public interface TaskFactory {
    @Nonnull
    AbstractTask createTask(@Nonnull TaskManager taskManager);
}
