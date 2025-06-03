package com.gym.facade;

import com.gym.model.Trainee;
import com.gym.model.Trainer;
import com.gym.model.Training;
import com.gym.service.TraineeService;
import com.gym.service.TrainerService;
import com.gym.service.TrainingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class GymFacade {

    private static final Logger logger = LogManager.getLogger(GymFacade.class);

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    // --- Trainee Operations ---
    public Trainee registerTrainee(Trainee trainee) {
        logger.info("Facade: Attempting to register trainee: {} {}", trainee.getFirstName(), trainee.getLastName());
        return traineeService.createTrainee(trainee);
    }

    public Trainee updateTraineeProfile(Trainee trainee) {
        logger.info("Facade: Attempting to update trainee profile for username: {}", trainee.getUsername());
        return traineeService.updateTrainee(trainee);
    }

    public void deleteTraineeAccount(String username) {
        logger.info("Facade: Attempting to delete trainee account for username: {}", username);
        traineeService.deleteTrainee(username);
    }

    public Trainee getTraineeDetails(String username) {
        logger.info("Facade: Retrieving trainee details for username: {}", username);
        return traineeService.getTraineeByUsername(username);
    }

    public List<Trainee> getAllTraineesList() {
        logger.info("Facade: Retrieving all trainees");
        return traineeService.getAllTrainees();
    }

    // --- Trainer Operations ---
    public Trainer registerTrainer(Trainer trainer) {
        logger.info("Facade: Attempting to register trainer: {} {}", trainer.getFirstName(), trainer.getLastName());
        return trainerService.createTrainer(trainer);
    }

    public Trainer updateTrainerProfile(Trainer trainer) {
        logger.info("Facade: Attempting to update trainer profile for username: {}", trainer.getUsername());
        return trainerService.updateTrainer(trainer);
    }

    public Trainer getTrainerDetails(String username) {
        logger.info("Facade: Retrieving trainer details for username: {}", username);
        return trainerService.getTrainerByUsername(username);
    }

    public List<Trainer> getAllTrainersList() {
        logger.info("Facade: Retrieving all trainers");
        return trainerService.getAllTrainers();
    }

    // --- Training Operations ---
    public Training scheduleTraining(Training training) {
        logger.info("Facade: Attempting to schedule training: {}", training.getTrainingName());
        return trainingService.createTraining(training);
    }

    public Training getTrainingSessionDetails(String trainingId) {
        logger.info("Facade: Retrieving training session details for ID: {}", trainingId);
        return trainingService.selectTrainingById(trainingId);
    }

    public List<Training> getAllScheduledTrainings() {
        logger.info("Facade: Retrieving all scheduled trainings");
        return trainingService.selectAllTrainings();
    }

    public boolean checkTrainerAvailability(String trainerUsername, LocalDate date) {
        logger.info("Facade: Checking availability for trainer {} on date {}", trainerUsername, date);
        Trainer trainer = trainerService.getTrainerByUsername(trainerUsername);
        if (trainer == null || !trainer.isActive()) {
            logger.warn("Facade: Trainer {} not found or inactive.", trainerUsername);
            return false;
        }

        List<Training> trainings = trainingService.selectAllTrainings();
        for (Training t : trainings) {
            if (t.getTrainer().getUsername().equals(trainerUsername) && t.getTrainingDate().equals(date))
                logger.info("Facade: Trainer {} has a training scheduled on {}. Further time slot check needed.", trainerUsername, date);
        }
        logger.info("Facade: Trainer {} appears available on {} (basic check).", trainerUsername, date);
        return true;
    }
}