package ru.javatalks.fundamentals.account.server;

import com.google.common.base.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/account/*")
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
        Optional<Integer> optionalId = getNumberFrom(req, resp);
        if (!optionalId.isPresent()) {
            return;
        }
        // Выполнить запрос
        Long amount = accountServiceJdbc.getAmount(optionalId.get());
        // Вернуть результат
        resp.getWriter().print(amount);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Получить значения параметров
        Optional<Integer> optionalId = getNumberFrom(req, resp);
        if (!optionalId.isPresent()) {
            return;
        }
        Long value = 0L;
        // Выполнить запрос
        accountServiceJdbc.addAmount(optionalId.get(), value);
        // Вернуть результат
    }

    @Nonnull
    private static Optional<Integer> getNumberFrom(@Nonnull HttpServletRequest req, @Nonnull HttpServletResponse resp)
            throws IOException {
        String pathInfo = req.getPathInfo();
        if (null == pathInfo) {
            resp.sendError(400, "Parameter not defined!");
            return Optional.absent();
        }
        String parameter = pathInfo.substring(1);
        Integer id;
        try {
            return Optional.of(Integer.valueOf(parameter));
        } catch (NumberFormatException e) {
            resp.sendError(400, "Unable to parse parameter: " + parameter);
            return Optional.absent();
        }
    }
}
