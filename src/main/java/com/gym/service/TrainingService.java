package com.gym.service;

import com.gym.model.Training;

import java.util.List;

public interface TrainingService {
    Training createTraining(Training training);
    Training selectTrainingById(String trainingId);
    List<Training> selectAllTrainings();
}