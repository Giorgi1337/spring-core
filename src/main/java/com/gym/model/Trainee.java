package com.gym.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Trainee extends User {

    @JsonProperty("dateOfBirth")
    private LocalDate dateOfBirth;

    @JsonProperty("address")
    private String address;

    @Override
    public String toString() {
        return String.format("Trainee [%s, DOB: %s, Address: %s]",
                super.toString(), dateOfBirth, address);
    }
}