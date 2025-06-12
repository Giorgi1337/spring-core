package com.gym.service;

import com.gym.model.Trainer;
import jakarta.validation.Valid;

public interface TrainerService {
    Trainer createTrainer(@Valid Trainer trainer);
    Trainer authenticateTrainer(String username, String password);
}
