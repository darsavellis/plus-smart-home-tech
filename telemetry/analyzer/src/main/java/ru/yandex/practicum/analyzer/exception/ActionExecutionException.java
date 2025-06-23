package ru.yandex.practicum.analyzer.exception;

public class ActionExecutionException extends RuntimeException {
    public ActionExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
