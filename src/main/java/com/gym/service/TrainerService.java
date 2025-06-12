package com.gym.service;

import com.gym.model.Trainer;
import jakarta.validation.Valid;

public interface TrainerService {
    Trainer createTrainer(@Valid Trainer trainer);
    Trainer authenticateTrainer(String username, String password);
    Trainer findByUsername(String username, String password);
    void changePassword(String username, String oldPassword, String newPassword);
    Trainer updateTrainer(String username, String password, @Valid Trainer updatedTrainer);
}
