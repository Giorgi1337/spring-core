package com.gym.service;

import com.gym.model.Trainee;

import java.util.List;

public interface TraineeService {
    Trainee createTrainee(Trainee trainee);
    Trainee updateTrainee(Trainee trainee);
    void deleteTrainee(String username);
    Trainee getTraineeByUsername(String username);
    List<Trainee> getAllTrainees();
}
