package ru.yandex.practicum.analyzer.dal.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "scenarios", uniqueConstraints = @UniqueConstraint(columnNames = {"hub_id", "name"}))
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "hub_id")
    String hubId;

    @Column(name = "name")
    String name;

    @OneToMany
    @MapKeyColumn(table = "scenario_conditions", name = "sensor_id")
    @JoinTable(
        name = "scenario_conditions",
        joinColumns = @JoinColumn(name = "scenario_id"),
        inverseJoinColumns = @JoinColumn(name = "condition_id")
    )
    @Builder.Default
    Map<String, Condition> conditions = new HashMap<>();

    @OneToMany
    @MapKeyColumn(table = "scenario_actions", name = "sensor_id")
    @JoinTable(
        name = "scenario_actions",
        joinColumns = @JoinColumn(name = "scenario_id"),
        inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    @Builder.Default
    Map<String, Action> actions = new HashMap<>();

    @Transient
    public void addCondition(String sensorId, Condition condition) {
        this.conditions.put(sensorId, condition);
    }

    @Transient
    public void addAction(String sensorId, Action action) {
        this.actions.put(sensorId, action);
    }
}
