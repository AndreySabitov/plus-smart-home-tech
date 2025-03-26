package ru.practicum.exceptions.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ErrorResponse handleException(final Exception e) {
        log.info("обработка исключения ", e);
        return new ErrorResponse(e.getMessage());
    }
}
