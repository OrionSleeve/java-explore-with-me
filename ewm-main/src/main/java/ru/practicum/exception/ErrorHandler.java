package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.event.exception.DateRangeException;
import ru.practicum.event.exception.EventException;
import ru.practicum.event.exception.EventStartTimeException;
import ru.practicum.ewm.Constants;
import ru.practicum.request.exception.RequestException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = ErrorResponse.builder().status(HttpStatus.BAD_REQUEST.toString())
                .reason("Incorrectly made request")
                .message(e.getMessage())
                .timestamp(formatter.format(LocalDateTime.now()))
                .build();
        log.error("Error occurred. Validation failed:{}", errorResponse);
        return errorResponse;

    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ErrorResponse errorResponse = ErrorResponse.builder().status(HttpStatus.BAD_REQUEST.toString())
                .reason("Incorrectly made request")
                .message(e.getMessage())
                .timestamp(formatter.format(LocalDateTime.now()))
                .build();
        log.error("Error occurred. Bad request:{}", errorResponse);
        return errorResponse;

    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        ErrorResponse errorResponse = ErrorResponse.builder().status(HttpStatus.CONFLICT.toString())
                .reason("Integrity constraint has been violated")
                .message(e.getMessage())
                .timestamp(formatter.format(LocalDateTime.now()))
                .build();
        log.error("Error occurred. Integrity constraint has been violated:{}", errorResponse);
        return errorResponse;
    }

    @ExceptionHandler(EventStartTimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidEventStartTimeException(EventStartTimeException e) {
        ErrorResponse errorResponse = ErrorResponse.builder().status(HttpStatus.BAD_REQUEST.toString())
                .reason("For the requested operation the conditions are not met")
                .message(e.getMessage())
                .timestamp(formatter.format(LocalDateTime.now()))
                .build();
        log.error("Error occurred. Invalid start time of the event:{}", errorResponse);
        return errorResponse;
    }

    @ExceptionHandler(RequestException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIllegalRequestOperationException(RequestException e) {
        ErrorResponse errorResponse = ErrorResponse.builder().status(HttpStatus.CONFLICT.toString())
                .reason("Trying to perform illegal creation of participation request")
                .message(e.getMessage())
                .timestamp(formatter.format(LocalDateTime.now()))
                .build();
        log.error("Error occurred. Illegal operation when creating participation request:{}", errorResponse);
        return errorResponse;
    }

    @ExceptionHandler(EventException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUnsupportedOperationException(EventException e) {
        ErrorResponse errorResponse = ErrorResponse.builder().status(HttpStatus.CONFLICT.toString())
                .reason("Operation is not supported")
                .message(e.getMessage())
                .timestamp(formatter.format(LocalDateTime.now()))
                .build();
        log.error("Error occurred. Unsupported operation:{}", errorResponse);
        return errorResponse;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder().status(HttpStatus.NOT_FOUND.toString())
                .reason("The required object was not found")
                .message(e.getMessage())
                .timestamp(formatter.format(LocalDateTime.now()))
                .build();
        log.error("Error occurred. The required object was not found:{}", errorResponse);
        return errorResponse;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestParameterException(MissingServletRequestParameterException e) {
        ErrorResponse errorResponse = ErrorResponse.builder().status(HttpStatus.BAD_REQUEST.toString())
                .reason("The required request parameter is missing")
                .message(e.getMessage())
                .timestamp(formatter.format(LocalDateTime.now()))
                .build();
        log.error("Error occurred. The required request parameter is missing:{}", errorResponse);
        return errorResponse;
    }

    @ExceptionHandler(DateRangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidDateRangeException(DateRangeException e) {
        ErrorResponse errorResponse = ErrorResponse.builder().status(HttpStatus.BAD_REQUEST.toString())
                .reason("Invalid date range in request")
                .message(e.getMessage())
                .timestamp(formatter.format(LocalDateTime.now()))
                .build();
        log.error("Error occurred. Invalid date range in request:{}", errorResponse);
        return errorResponse;
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(Throwable t) {
        ErrorResponse errorResponse = ErrorResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .reason("Internal server error occurred")
                .message(t.getMessage())
                .timestamp(formatter.format(LocalDateTime.now()))
                .build();
        log.error("Internal server error occurred:{}", errorResponse);
        return errorResponse;
    }
}
