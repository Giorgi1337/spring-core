package com.gym.service;

import com.gym.model.Trainee;
import jakarta.validation.Valid;

public interface TraineeService {
    Trainee createTrainee(@Valid Trainee trainee);
    Trainee authenticateTrainee(String username, String password);
    Trainee findByUsername(String username, String password);
    void changePassword(String username, String oldPassword, String newPassword);
    Trainee updateTrainee(String username, String password,@Valid Trainee updatedTrainee);
}
