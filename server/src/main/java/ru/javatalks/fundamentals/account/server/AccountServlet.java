package ru.javatalks.fundamentals.account.server;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {
    @Resource(name = "jdbc/accountDB")
    private DataSource dataSource;

    private AccountServiceJdbc accountServiceJdbc;

    @Override
    public void init() throws ServletException {
        accountServiceJdbc = new AccountServiceJdbc(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Получить значения параметров
        Integer id = 0;
        req.getServletPath();
        req.getContextPath();
        // Выполнить запрос
        Long amount = accountServiceJdbc.getAmount(id);
        // Вернуть результат
        resp.getWriter().print(amount);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Получить значения параметров
        Integer id = 0;
        Long value = 0L;
        // Выполнить запрос
        accountServiceJdbc.addAmount(id, value);
        // Вернуть результат
    }
}
