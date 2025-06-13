package com.gym.service;

import com.gym.model.Trainee;
import com.gym.model.Trainer;
import com.gym.model.Training;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TraineeService {
    Trainee createTrainee(@Valid Trainee trainee);
    Trainee authenticateTrainee(String username, String password);
    Trainee findByUsername(String username, String password);
    void changePassword(String username, String oldPassword, String newPassword);
    Trainee updateTrainee(String username, String password,@Valid Trainee updatedTrainee);
    Trainee toggleTraineeActiveStatus(String username, String password);
    Trainee deleteTrainee(String username, String password);


    List<Training> getTraineeTrainingsList(String username, String password, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingTypeName);

    Set<Trainer> getUnassignedTrainers(String username, String password);

    Trainee updateTraineeTrainersList(String traineeUsername, String password, List<String> trainerUsernames);
}
