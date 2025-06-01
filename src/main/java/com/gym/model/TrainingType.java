package com.gym.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TrainingType {
    CARDIO("Cardio"),
    STRENGTH("Strength Training"),
    YOGA("Yoga"),
    PILATES("Pilates"),
    CROSSFIT("CrossFit"),
    BOXING("Boxing"),
    POWERLIFTING("Powerlifting");

    private final String type;

    TrainingType(String type) {
        this.type = type;
    }

    @Override
    @JsonValue
    public String toString() {
        return type;
    }

    @JsonCreator
    public static TrainingType fromType(String type) {
        return Arrays.stream(values())
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown training type: " + type));
    }
}