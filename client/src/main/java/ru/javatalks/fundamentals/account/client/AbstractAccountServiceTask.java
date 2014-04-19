package ru.javatalks.fundamentals.account.client;

import lombok.extern.java.Log;
import ru.javatalks.fundamentals.account.AccountService;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.logging.Level.WARNING;

@Log
public abstract class AbstractAccountServiceTask extends AbstractTask {
    private final AccountService accountService;
    private final int minId;
    private final int maxId;
    private ThreadLocalRandom threadLocalRandom;

    protected AbstractAccountServiceTask(@Nonnull TaskManager taskManager, @Nonnull String url, int minId, int maxId) {
        super(taskManager);
        this.minId = minId;
        this.maxId = maxId;
        accountService = new AccountServiceClient(url);
    }

    @Override
    protected final void init() {
        threadLocalRandom = ThreadLocalRandom.current();
    }

    @Override
    protected final void perform() {
        try {
            withAccountService(accountService);
        } catch (HttpClientException e) {
            log.log(WARNING, "Unable to perform operation with account service", e);
        }

    }

    protected abstract void withAccountService(@Nonnull AccountService accountService);

    protected int getRandomId() {
        return threadLocalRandom.nextInt(minId, maxId + 1);
    }

    protected long getRandomValue() {
        return threadLocalRandom.nextLong(1, 100);
    }
}
