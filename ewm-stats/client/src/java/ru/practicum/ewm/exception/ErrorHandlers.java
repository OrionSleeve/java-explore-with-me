package java.ru.practicum.ewm.exception;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class ErrorHandlers implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is5xxServerError() ||
                response.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        throw new ErrorException(String.format("Exception invoked when receiving data " +
                        "from stats server. Status code: %s. Status text: %s", response.getStatusCode(),
                response.getStatusText()));
    }
}
