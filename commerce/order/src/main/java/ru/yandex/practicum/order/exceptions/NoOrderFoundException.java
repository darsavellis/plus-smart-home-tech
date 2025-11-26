package ru.yandex.practicum.order.exceptions;

public class NoOrderFoundException extends RuntimeException {
    public NoOrderFoundException(String message) {
        super(message);
    }
}
