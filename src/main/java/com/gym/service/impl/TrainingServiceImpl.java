package com.gym.service.impl;

import com.gym.dao.TraineeDao;
import com.gym.dao.TrainerDao;
import com.gym.dao.TrainingDao;
import com.gym.dao.TrainingTypeDao;
import com.gym.exception.AuthenticationException;
import com.gym.exception.TraineeNotFoundException;
import com.gym.exception.TrainerNotFoundException;
import com.gym.exception.TrainingTypeNotFoundException;
import com.gym.model.Trainee;
import com.gym.model.Trainer;
import com.gym.model.Training;
import com.gym.model.TrainingType;
import com.gym.service.TrainingService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private static final Logger logger = LogManager.getLogger(TrainingServiceImpl.class);

    private final TrainingDao trainingDao;
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingTypeDao trainingTypeDao;

    @Override
    public Training addTraining(String traineeUsername, String traineePassword,
                                String trainerUsername, String trainerPassword,
                                @Valid Training training) {
        logger.info("[TRAINING_SERVICE] [ADD] Attempting to add training for trainee={} and trainer={}",
                traineeUsername, trainerUsername);

        // Authenticate trainee and trainer (as per requirement 2)
        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee with username " + traineeUsername + " not found."));
        if (!traineePassword.equals(trainee.getUser().getPassword())) {
            throw new AuthenticationException("Invalid password for trainee " + traineeUsername);
        }

        Trainer trainer = trainerDao.findByUsername(trainerUsername)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer with username " + trainerUsername + " not found."));
        if (!trainerPassword.equals(trainer.getUser().getPassword())) {
            throw new AuthenticationException("Invalid password for trainer " + trainerUsername);
        }

        TrainingType trainingType = trainingTypeDao.findByTrainingTypeName(training.getTrainingType().getTrainingTypeName())
                .orElseThrow(() -> {
                    logger.warn("[TRAINING_SERVICE] [ADD] Training type not found: name={}", training.getTrainingType().getTrainingTypeName());
                    return new TrainingTypeNotFoundException("Training Type with name " + training.getTrainingType().getTrainingTypeName() + " not found.");
                });


        // Set the relationships
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);

        trainingDao.save(training);

        // Also add to the sets in Trainee and Trainer
        trainee.getTrainings().add(training);
        trainer.getTrainings().add(training);

        // Ensure the many-to-many relationship is established
        if (!trainee.getTrainers().contains(trainer)) {
            trainee.getTrainers().add(trainer);
            trainer.getTrainees().add(trainee);
        }

        logger.info("[TRAINING_SERVICE] [ADD] Training added successfully for trainee={} and trainer={}",
                traineeUsername, trainerUsername);
        return training;
    }
}