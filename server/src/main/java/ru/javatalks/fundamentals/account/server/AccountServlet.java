package ru.javatalks.fundamentals.account.server;

import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Charsets.UTF_8;

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
        Optional<Integer> optionalId = getIntegerFromPathInfo(req, resp);
        if (!optionalId.isPresent()) {
            return;
        }

        Long amount = accountServiceJdbc.getAmount(optionalId.get());

        resp.getWriter().print(amount);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<Integer> optionalId = getIntegerFromPathInfo(req, resp);
        if (!optionalId.isPresent()) {
            return;
        }

        Optional<Long> optionalValue = getLongFromInput(req, resp);
        if (!optionalValue.isPresent()) {
            return;
        }

        accountServiceJdbc.addAmount(optionalId.get(), optionalValue.get());
    }

    @Nonnull
    private static Optional<Integer> getIntegerFromPathInfo(@Nonnull HttpServletRequest req,
                                                            @Nonnull HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (null == pathInfo) {
            resp.sendError(400, "Parameter not defined!");
            return Optional.absent();
        }
        String parameter = pathInfo.substring(1);
        try {
            return Optional.of(Integer.valueOf(parameter));
        } catch (NumberFormatException e) {
            resp.sendError(400, "Unable to parse parameter: " + parameter);
            return Optional.absent();
        }
    }

    @Nonnull
    private static Optional<Long> getLongFromInput(@Nonnull HttpServletRequest req,
                                                   @Nonnull HttpServletResponse resp) throws IOException {
        String valueStr = getRequestInput(req);
        try {
            return Optional.of(Long.valueOf(valueStr));
        } catch (NumberFormatException e) {
            resp.sendError(400, "Unable to parse parameter: " + valueStr);
            return Optional.absent();
        }
    }

    @Nonnull
    private static String getRequestInput(HttpServletRequest req) throws IOException {
        try (InputStream inputStream = req.getInputStream()) {
            return new String(ByteStreams.toByteArray(inputStream), UTF_8);
        }
    }
}
