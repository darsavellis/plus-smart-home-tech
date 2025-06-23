package ru.yandex.practicum.analyzer.exception;

public class UnknownDeviceException extends RuntimeException {
    public UnknownDeviceException(String hubId) {
        super(String.format("Unknown device for hub '%s'.", hubId));
    }
}
