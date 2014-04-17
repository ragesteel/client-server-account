package ru.javatalks.fundamentals.account;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public interface AccountService {
    /**
     * Возвращает текущий баланс или 0 если addAmount() не был вызван перед этим для заданного id.
     */
    @CheckReturnValue
    @Nonnull
    Long getAmount(@Nonnull Integer id);

    /**
     * Увеличивает баланс или устанавливает его, если addAmount() вызывается в первый раз
     */
    void addAmount(@Nonnull Integer id, @Nonnull Long value);
}
