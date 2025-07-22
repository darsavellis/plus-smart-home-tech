package ru.yandex.practicum.analyzer.dal.model;

import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

import java.util.Optional;

public enum ConditionOperation implements Operation {
    EQUALS {
        @Override
        public boolean apply(Integer left, Integer right) {
            return Optional.ofNullable(left)
                    .flatMap(l -> Optional.ofNullable(right)
                            .map(r -> l.compareTo(r) == 0))
                    .orElse(false);
        }
    },
    GREATER_THAN {
        @Override
        public boolean apply(Integer left, Integer right) {
            return Optional.ofNullable(left)
                    .flatMap(l -> Optional.ofNullable(right)
                            .map(r -> l.compareTo(r) > 0))
                    .orElse(false);
        }
    },
    LOWER_THAN {
        @Override
        public boolean apply(Integer left, Integer right) {
            return Optional.ofNullable(left)
                    .flatMap(l -> Optional.ofNullable(right)
                            .map(r -> l.compareTo(r) < 0))
                    .orElse(false);
        }
    };

    public static ConditionOperation from(ConditionOperationAvro operation) {
        if (operation == null) {
            throw new IllegalArgumentException("Operation cannot be null");
        }
        return valueOf(operation.name().toUpperCase());
    }
}
