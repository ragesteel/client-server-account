package ru.javatalks.fundamentals.account.client;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.google.common.base.Charsets.UTF_8;
import static java.util.Objects.requireNonNull;

/**
 * Простая и наивная обёртка вокруг http-подключения для операций GET и PUT.
 */
public class SimpleHttpClient {
    private final String url;

    public SimpleHttpClient(String url) {
        this.url = requireNonNull(url);
    }

    @Nonnull
    public String doGetRequest(@Nonnull String urlSuffix) throws HttpClientException {
        try {
            HttpURLConnection connection = createConnection(requireNonNull(urlSuffix), "GET", null);

            try (InputStream inputStream = connection.getInputStream()) {
                return new String(ByteStreams.toByteArray(inputStream), UTF_8);
            }
        } catch (IOException e) {
            throw new HttpClientException("Unable to perform request", e);
        }
    }

    public void doPutRequest(@Nonnull String urlSuffix, @Nonnull String output) throws HttpClientException {
        try {
            createConnection(requireNonNull(urlSuffix), "PUT", requireNonNull(output));
        } catch (IOException e) {
            throw new HttpClientException("Unable to perform request", e);
        }
    }

    @Nonnull
    private HttpURLConnection createConnection(@Nonnull String urlSuffix, @Nonnull String method,
                                               @Nullable String output) throws IOException {
        HttpURLConnection result = (HttpURLConnection) new URL(url + urlSuffix).openConnection();
        result.setRequestMethod(method);
        // Приводим пустую строку к null, чтобы засунуть обращение в if с проверкой на null,
        // Чтобы IDEA потом не ругалася на то, что output может быть null.
        String outputOrNull = Strings.emptyToNull(output);
        if (null != outputOrNull) {
            result.setDoOutput(true);
        }
        result.connect();
        if (null != outputOrNull) {
            try (OutputStream outputStream = result.getOutputStream()) {
                outputStream.write(outputOrNull.getBytes(UTF_8));
            }
        }
        int responseCode = result.getResponseCode();
        if (200 != responseCode) {
            throw new HttpClientException("ResponseCode is not equals 200, got " + responseCode);
        }
        return result;
    }
}
