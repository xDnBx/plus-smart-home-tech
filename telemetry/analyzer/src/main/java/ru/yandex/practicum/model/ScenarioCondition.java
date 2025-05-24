package ru.yandex.practicum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "scenario_conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@IdClass(ScenarioConditionId.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioCondition {
    @Id
    @ManyToOne
    @JoinColumn(name = "scenario_id")
    Scenario scenario;

    @Id
    @ManyToOne
    @JoinColumn(name = "sensor_id")
    Sensor sensor;

    @Id
    @ManyToOne
    @JoinColumn(name = "condition_id")
    Condition condition;
}