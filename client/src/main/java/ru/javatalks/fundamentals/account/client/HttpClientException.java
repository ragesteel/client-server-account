package ru.javatalks.fundamentals.account.client;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public class HttpClientException extends RuntimeException {
    public HttpClientException(@Nonnull String message) {
        super(requireNonNull(message));
    }

    public HttpClientException(@Nonnull String message, @Nonnull Throwable cause) {
        super(requireNonNull(message), requireNonNull(cause));
    }
}
