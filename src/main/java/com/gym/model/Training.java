package com.gym.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Training {

    @JsonProperty("trainee")
    private Trainee trainee;

    @JsonProperty("trainer")
    private Trainer trainer;

    @JsonProperty("trainingName")
    private String trainingName;

    @JsonProperty("trainingType")
    private TrainingType trainingType;

    @JsonProperty("trainingDate")
    private LocalDate trainingDate;

    @JsonProperty("trainingDuration")
    private int trainingDuration;

    @Override
    public String toString() {
        return String.format("Training [Trainee: %s → Trainer: %s, Name: %s, Type: %s, Date: %s, Duration: %d min]",
                trainee.getUsername(), trainer.getUsername(),
                trainingName, trainingType, trainingDate, trainingDuration);
    }
}