package ru.yandex.practicum.analyzer.exception;

public class ScenarioNotFoundException extends RuntimeException {
    public ScenarioNotFoundException(String name, String hubId) {
        super(String.format("Scenario with name '%s' for hub '%s' not found.", name, hubId));
    }
}
