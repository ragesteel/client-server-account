package ru.javatalks.fundamentals.account.client;

import ru.javatalks.fundamentals.account.AccountService;

public class AccountServiceClientSandbox {
    public static void main(String[] args) {
        AccountService accountService = new AccountServiceClient("http://localhost:8080/account-service/account/");
        Integer id = 1;
        accountService.addAmount(id, 100L);
        System.out.println(accountService.getAmount(id));
    }
}
