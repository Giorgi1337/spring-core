package com.gym.service.impl;

import com.gym.dao.TrainerDao;
import com.gym.exception.AuthenticationException;
import com.gym.exception.TrainerNotFoundException;
import com.gym.model.Trainer;
import com.gym.model.Training;
import com.gym.service.TrainerService;
import com.gym.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Validated
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LogManager.getLogger(TrainerServiceImpl.class);

    private final UserService userService;
    private final TrainerDao trainerDao;

    @Override
    public Trainer createTrainer(@Valid Trainer trainer) {
        logger.info("[TRAINER_SERVICE] [CREATE] Starting creation: firstName={} lastName={}",
                trainer.getUser().getFirstName(), trainer.getUser().getLastName());

        userService.setupNewUser(trainer.getUser());
        trainerDao.save(trainer);

        logger.info("[TRAINER_SERVICE] [CREATE] Created trainer successfully: username={}", trainer.getUser().getUsername());
        return trainer;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer authenticateTrainer(String username, String password) {
        logger.info("[TRAINER_SERVICE] [AUTH] Attempting authentication: username={}", username);
        return authenticateAndGet(username, password);
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer findByUsername(String username, String password) {
        logger.info("[TRAINER_SERVICE] [FIND] Finding trainer by username: {}", username);
        return authenticateAndGet(username, password);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        logger.info("[TRAINER_SERVICE] [PASSWORD_CHANGE] Attempting password change: username={}", username);

        Trainer trainer = authenticateAndGet(username, oldPassword);
        trainer.getUser().setPassword(newPassword);
        trainerDao.update(trainer);

        logger.info("[TRAINER_SERVICE] [PASSWORD_CHANGE] Password changed successfully: username={}", username);
    }

    @Override
    public Trainer updateTrainer(String username, String password, Trainer updatedTrainer) {
        logger.info("[TRAINER_SERVICE] [UPDATE] Starting update for: username={}", username);

        Trainer existing = authenticateAndGet(username, password);
        userService.updateUserDetails(existing.getUser(), updatedTrainer.getUser());
        existing.setSpecialization(updatedTrainer.getSpecialization());

        trainerDao.update(existing);

        logger.info("[TRAINER_SERVICE] [UPDATE] Updated trainer profile successfully: username={}", existing.getUser().getUsername());
        return existing;
    }

    @Override
    public Trainer toggleTrainerActiveStatus(String username, String password) {
        logger.info("[TRAINER_SERVICE] [STATUS_TOGGLE] Toggling active status: username={}", username);

        Trainer trainer = authenticateAndGet(username, password);
        boolean newStatus = !Boolean.TRUE.equals(trainer.getUser().getIsActive());
        trainer.getUser().setIsActive(newStatus);

        trainerDao.update(trainer);

        logger.info("[TRAINER_SERVICE] [STATUS_TOGGLE] Status updated: username={} isActive={}", username, newStatus);
        return trainer;
    }

    @Override
    public Trainer deleteTrainer(String username, String password) {
        logger.info("[TRAINER_SERVICE] [DELETE] Attempting deletion: username={}", username);

        Trainer trainer = authenticateAndGet(username, password);
        trainerDao.delete(trainer);

        logger.info("[TRAINER_SERVICE] [DELETE] Trainer deleted successfully: username={}", username);
        return trainer;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainingsList(String username, String password, LocalDate fromDate, LocalDate toDate, String traineeName) {
        logger.info("[TRAINER_SERVICE] [GET_TRAININGS] Fetching trainings for trainer: username={}", username);
        authenticateAndGet(username, password);
        return trainerDao.findTrainerTrainingsWithCriteria(username, fromDate, toDate, traineeName);
    }

    private Trainer authenticateAndGet(String username, String password) {
        Trainer trainer = trainerDao.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("[TRAINER_SERVICE] [AUTH] Trainer not found: username={}", username);
                    return new TrainerNotFoundException("Trainer with username " + username + " not found");
                });

        if (trainer.getUser() == null || !password.equals(trainer.getUser().getPassword())) {
            logger.warn("[TRAINER_SERVICE] [AUTH] Authentication failed: username={}", username);
            throw new AuthenticationException("Invalid username or password");
        }

        logger.info("[TRAINER_SERVICE] [AUTH] Authenticated successfully: username={}", username);
        return trainer;
    }
}