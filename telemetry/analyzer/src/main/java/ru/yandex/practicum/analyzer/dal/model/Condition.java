package ru.yandex.practicum.analyzer.dal.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "conditions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    ConditionTypeAvro type;

    @Column(name = "operation")
    @Enumerated(EnumType.STRING)
    ConditionOperation operation;

    @Column(name = "value")
    Integer value;

    @Transient
    public boolean check(int sensorValue) {
        return operation.apply(sensorValue, this.value);
    }
}
