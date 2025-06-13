package com.gym.service;

import com.gym.model.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeService {
    TrainingType createTrainingType(TrainingType trainingType);
    Optional<TrainingType> getTrainingTypeById(Long id);
    Optional<TrainingType> getTrainingTypeByName(String name);
    List<TrainingType> getAllTrainingTypes();
}
