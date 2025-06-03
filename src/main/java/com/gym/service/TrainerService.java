package com.gym.service;

import com.gym.model.Trainer;

import java.util.List;

public interface TrainerService {
    Trainer createTrainer(Trainer trainer);
    Trainer updateTrainer(Trainer trainer);
    List<Trainer> getAllTrainers();
    Trainer getTrainerByUsername(String username);
}
