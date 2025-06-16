package com.gym.service;

import com.gym.model.Training;
import jakarta.validation.Valid;

public interface TrainingService {
    Training addTraining(
            String traineeUsername,
            String traineePassword,
            String trainerUsername,
            String trainerPassword,
            @Valid Training training
    );
}