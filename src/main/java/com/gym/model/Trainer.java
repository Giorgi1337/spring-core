package com.gym.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Trainer extends User {

    @JsonProperty("specialization")
    private String specialization;

    @Override
    public String toString() {
        return String.format("Trainer [%s, Specialization: %s]",
                super.toString(), specialization);
    }
}