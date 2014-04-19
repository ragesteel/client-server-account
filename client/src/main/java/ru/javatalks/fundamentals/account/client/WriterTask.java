package ru.javatalks.fundamentals.account.client;

import ru.javatalks.fundamentals.account.AccountService;

import javax.annotation.Nonnull;

public class WriterTask extends AbstractAccountServiceTask {
    public WriterTask(@Nonnull TaskManager taskManager, @Nonnull String url, int minId, int maxId) {
        super(taskManager, url, minId, maxId);
    }

    @Override
    protected void withAccountService(@Nonnull AccountService accountService) {
        accountService.addAmount(getRandomId(), getRandomValue());
    }
}
