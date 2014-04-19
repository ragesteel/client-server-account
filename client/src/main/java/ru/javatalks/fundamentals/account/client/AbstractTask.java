package ru.javatalks.fundamentals.account.client;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public abstract class AbstractTask implements Runnable {
    private final TaskManager taskManager;

    protected AbstractTask(@Nonnull TaskManager taskManager) {
        this.taskManager = requireNonNull(taskManager);
    }

    @Override
    public final void run() {
        taskManager.started();
        while (taskManager.isRunning()) {
            perform();
        }
        taskManager.stopped();
    }

    protected abstract void perform();
}
