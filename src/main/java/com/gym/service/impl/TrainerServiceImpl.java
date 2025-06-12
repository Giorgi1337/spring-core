package com.gym.service.impl;

import com.gym.dao.TrainerDao;
import com.gym.exception.AuthenticationException;
import com.gym.exception.TrainerNotFoundException;
import com.gym.model.Trainer;
import com.gym.service.TrainerService;
import com.gym.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

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
        logger.info("[TRAINER] Creating new trainer: {} {}",
                trainer.getUser().getFirstName(), trainer.getUser().getLastName());

        userService.setupNewUser(trainer.getUser());
        trainerDao.save(trainer);

        logger.info("[TRAINER] Created trainer with username: {}", trainer.getUser().getUsername());
        return trainer;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer authenticateTrainer(String username, String password) {
        return authenticateAndGet(username, password);
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer findByUsername(String username, String password) {
        return authenticateAndGet(username, password);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainer trainer = authenticateAndGet(username, oldPassword);
        trainer.getUser().setPassword(newPassword);
        trainerDao.update(trainer);

        logger.info("[TRAINER] Password changed successfully for: {}", username);
    }

    @Override
    public Trainer updateTrainer(String username, String password, Trainer updatedTrainer) {
        Trainer existing = authenticateAndGet(username, password);

        userService.updateUserDetails(existing.getUser(), updatedTrainer.getUser());

        existing.setSpecialization(updatedTrainer.getSpecialization());

        trainerDao.update(existing);

        logger.info("[TRAINER] Updated profile for: {}", existing.getUser().getUsername());
        return existing;
    }

    @Override
    public Trainer toggleTrainerActiveStatus(String username, String password) {
        Trainer trainer = authenticateAndGet(username, password);

        boolean newStatus = !Boolean.TRUE.equals(trainer.getUser().getIsActive());
        trainer.getUser().setIsActive(newStatus);

        trainerDao.update(trainer);
        logger.info("[TRAINER] Toggled active status to {} for: {}", newStatus, username);

        return trainer;
    }

    @Override
    public Trainer deleteTrainer(String username, String password) {
        Trainer trainer = authenticateAndGet(username, password);
        trainerDao.delete(trainer);

        logger.info("[TRAINER] Deleted trainer with username: {}", username);
        return trainer;
    }

    private Trainer authenticateAndGet(String username, String password) {
        Trainer trainer = trainerDao.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("[TRAINER] No trainer found for username: {}", username);
                    return new TrainerNotFoundException("Trainer with username " + username + " not found");
                });

        if (trainer.getUser() == null || !password.equals(trainer.getUser().getPassword())) {
            logger.warn("[TRAINER] Authentication failed for: {}", username);
            throw new AuthenticationException("Invalid username or password");
        }

        logger.info("[TRAINER] Authenticated: {}", username);
        return trainer;
    }

}