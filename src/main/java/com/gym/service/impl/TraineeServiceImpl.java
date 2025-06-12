package com.gym.service.impl;

import com.gym.dao.TraineeDao;
import com.gym.exception.AuthenticationException;
import com.gym.exception.TraineeNotFoundException;
import com.gym.model.Trainee;
import com.gym.service.TraineeService;
import com.gym.service.UserService;
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
public class TraineeServiceImpl implements TraineeService {
    private static final Logger logger = LogManager.getLogger(TraineeServiceImpl.class);

    private final UserService userService;
    private final TraineeDao traineeDao;

    @Override
    public Trainee createTrainee(Trainee trainee) {
        logger.info("[TRAINEE] Creating new trainee: {} {}",
                trainee.getUser().getFirstName(), trainee.getUser().getLastName());

        userService.setupNewUser(trainee.getUser());
        traineeDao.save(trainee);

        logger.info("[TRAINEE] Created trainee with username: {}", trainee.getUser().getUsername());
        return trainee;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee authenticateTrainee(String username, String password) {
        return authenticateAndGet(username, password);
    }

    private Trainee authenticateAndGet(String username, String password) {
        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("[TRAINEE] No trainee found for username: {}", username);
                    return new TraineeNotFoundException("Trainee with username " + username + " not found");
                });

        if (trainee.getUser() == null || !password.equals(trainee.getUser().getPassword())) {
            logger.warn("[TRAINEE] Authentication failed for: {}", username);
            throw new AuthenticationException("Invalid username or password");
        }

        logger.info("[TRAINEE] Authenticated: {}", username);
        return trainee;
    }

}