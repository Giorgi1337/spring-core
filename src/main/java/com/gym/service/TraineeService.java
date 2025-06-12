package com.gym.service;

import com.gym.model.Trainee;
import jakarta.validation.Valid;

public interface TraineeService {
    Trainee createTrainee(@Valid Trainee trainee);
    Trainee authenticateTrainee(String username, String password);
}
