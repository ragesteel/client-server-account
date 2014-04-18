package ru.javatalks.fundamentals.account.client;

import ru.javatalks.fundamentals.account.AccountService;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public class AccountServiceClient implements AccountService {
    private final SimpleHttpClient simpleHttpClient;

    public AccountServiceClient(@Nonnull String url) {
        simpleHttpClient = new SimpleHttpClient(url);
    }

    @Nonnull
    @Override
    public Long getAmount(@Nonnull Integer id) {
        requireNonNull(id);
        return Long.valueOf(simpleHttpClient.doGetRequest(id.toString()));
    }

    @Override
    public void addAmount(@Nonnull Integer id, @Nonnull Long value) {
        requireNonNull(id);
        requireNonNull(value);
        simpleHttpClient.doPutRequest(id.toString(), value.toString());
    }
}
