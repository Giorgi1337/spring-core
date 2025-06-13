package com.gym.service.impl;

import com.gym.dao.TraineeDao;
import com.gym.dao.TrainerDao;
import com.gym.dao.TrainingDao;
import com.gym.exception.*;
import com.gym.model.Trainee;
import com.gym.model.Trainer;
import com.gym.model.Training;
import com.gym.service.TraineeService;
import com.gym.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Validated
public class TraineeServiceImpl implements TraineeService {

    private static final Logger logger = LogManager.getLogger(TraineeServiceImpl.class);

    private final UserService userService;
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingDao trainingDao;

    @Override
    public Trainee createTrainee(Trainee trainee) {
        logger.info("[TRAINEE_SERVICE] [CREATE] Starting creation: firstName={} lastName={}",
                trainee.getUser().getFirstName(), trainee.getUser().getLastName());

        userService.setupNewUser(trainee.getUser());
        traineeDao.save(trainee);

        logger.info("[TRAINEE_SERVICE] [CREATE] Created trainee: username={}", trainee.getUser().getUsername());
        return trainee;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee authenticateTrainee(String username, String password) {
        logger.info("[TRAINEE_SERVICE] [AUTH] Attempting authentication: username={}", username);
        return authenticateAndGet(username, password);
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee findByUsername(String username, String password) {
        logger.info("[TRAINEE_SERVICE] [FIND] Finding trainee by username: {}", username);
        return authenticateAndGet(username, password);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        logger.info("[TRAINEE_SERVICE] [PASSWORD_CHANGE] Attempting password change: username={}", username);
        Trainee trainee = authenticateAndGet(username, oldPassword);
        trainee.getUser().setPassword(newPassword);
        traineeDao.update(trainee);

        logger.info("[TRAINEE_SERVICE] [PASSWORD_CHANGE] Password successfully changed: username={}", username);
    }

    @Override
    public Trainee updateTrainee(String username, String password, Trainee updatedTrainee) {
        logger.info("[TRAINEE_SERVICE] [UPDATE] Starting update for: username={}", username);
        Trainee existing = authenticateAndGet(username, password);

        userService.updateUserDetails(existing.getUser(), updatedTrainee.getUser());
        existing.setAddress(updatedTrainee.getAddress());
        existing.setDateOfBirth(updatedTrainee.getDateOfBirth());

        traineeDao.update(existing);
        logger.info("[TRAINEE_SERVICE] [UPDATE] Profile updated successfully: username={}", existing.getUser().getUsername());

        return existing;
    }

    @Override
    public Trainee toggleTraineeActiveStatus(String username, String password) {
        logger.info("[TRAINEE_SERVICE] [STATUS_TOGGLE] Toggling active status: username={}", username);
        Trainee trainee = authenticateAndGet(username, password);

        boolean newStatus = !Boolean.TRUE.equals(trainee.getUser().getIsActive());
        trainee.getUser().setIsActive(newStatus);
        traineeDao.update(trainee);

        logger.info("[TRAINEE_SERVICE] [STATUS_TOGGLE] Status updated: username={} isActive={}", username, newStatus);
        return trainee;
    }

    @Override
    public Trainee deleteTrainee(String username, String password) {
        logger.info("[TRAINEE_SERVICE] [DELETE] Attempting deletion: username={}", username);
        Trainee trainee = authenticateAndGet(username, password);
        traineeDao.delete(trainee);

        logger.info("[TRAINEE_SERVICE] [DELETE] Trainee deleted: username={}", username);
        return trainee;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainingsList(String username, String password, LocalDateTime   fromDate, LocalDateTime  toDate, String trainerName, String trainingTypeName) {
        logger.info("[TRAINEE_SERVICE] [GET_TRAININGS] Fetching trainings for trainee: username={}", username);
        authenticateAndGet(username, password);
        return trainingDao.findByTraineeUsernameAndCriteria(username, fromDate, toDate, trainerName, trainingTypeName);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Trainer> getUnassignedTrainers(String username, String password) {
        logger.info("[TRAINEE_SERVICE] [GET_UNASSIGNED_TRAINERS] Fetching unassigned trainers for trainee: username={}", username);
        authenticateAndGet(username, password);

        List<Trainer> unassignedTrainers = traineeDao.findUnassignedTrainersCriteria(username);
        return new HashSet<>(unassignedTrainers);
    }

    @Override
    public Trainee updateTraineeTrainersList(String username, String password, List<String> trainerUsernames) {
        logger.info("[TRAINEE_SERVICE] [UPDATE_TRAINERS_LIST] Updating trainers list for trainee: username={}", username);
        Trainee trainee = authenticateAndGet(username, password);

        // Validate that all trainer usernames exist and are active
        Set<Trainer> newTrainers = new HashSet<>();
        for (String trainerUsername : trainerUsernames) {
            Optional<Trainer> trainerOpt = trainerDao.findByUsername(trainerUsername);
            if (trainerOpt.isEmpty()) {
                logger.warn("[TRAINEE_SERVICE] [UPDATE_TRAINERS_LIST] Trainer not found: username={}", trainerUsername);
                throw new TrainerNotFoundException("Trainer with username " + trainerUsername + " not found");
            }

            Trainer trainer = trainerOpt.get();
            if (!Boolean.TRUE.equals(trainer.getUser().getIsActive())) {
                logger.warn("[TRAINEE_SERVICE] [UPDATE_TRAINERS_LIST] Trainer is not active: username={}", trainerUsername);
                throw new IllegalArgumentException("Trainer " + trainerUsername + " is not active and cannot be assigned");
            }

            newTrainers.add(trainer);
        }

        // Clear existing trainers and set new ones
        Set<Trainer> currentTrainers = trainee.getTrainers();
        Set<Trainer> trainersToRemove = new HashSet<>(currentTrainers);
        trainersToRemove.removeAll(newTrainers);

        Set<Trainer> trainersToAdd = new HashSet<>(newTrainers);
        trainersToAdd.removeAll(currentTrainers);

        // Remove old associations
        for (Trainer trainer : trainersToRemove) {
            currentTrainers.remove(trainer);
            trainer.getTrainees().remove(trainee);
        }

        // Add new associations
        for (Trainer trainer : trainersToAdd) {
            currentTrainers.add(trainer);
            trainer.getTrainees().add(trainee);
        }

        traineeDao.update(trainee);
        logger.info("[TRAINEE_SERVICE] [UPDATE_TRAINERS_LIST] Trainers list updated for trainee: username={}, added={}, removed={}",
                username, trainersToAdd.size(), trainersToRemove.size());
        return trainee;
    }

    private Trainee authenticateAndGet(String username, String password) {
        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("[TRAINEE_SERVICE] [AUTH] Trainee not found: username={}", username);
                    return new TraineeNotFoundException("Trainee with username " + username + " not found");
                });

        if (trainee.getUser() == null || !password.equals(trainee.getUser().getPassword())) {
            logger.warn("[TRAINEE_SERVICE] [AUTH] Authentication failed: username={}", username);
            throw new AuthenticationException("Invalid username or password");
        }

        logger.info("[TRAINEE_SERVICE] [AUTH] Authenticated successfully: username={}", username);
        return trainee;
    }
}