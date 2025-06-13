package com.gym.service;

import com.gym.model.Trainer;
import com.gym.model.Training;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

public interface TrainerService {
    Trainer createTrainer(@Valid Trainer trainer);
    Trainer authenticateTrainer(String username, String password);
    Trainer findByUsername(String username, String password);
    void changePassword(String username, String oldPassword, String newPassword);
    Trainer updateTrainer(String username, String password, @Valid Trainer updatedTrainer);
    Trainer toggleTrainerActiveStatus(String username, String password);
    Trainer deleteTrainer(String username, String password);

    List<Training> getTrainerTrainingsList(String username, String password, LocalDate fromDate, LocalDate toDate, String traineeName);
}
