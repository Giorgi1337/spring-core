package com.gym.service.impl;

import com.gym.dao.Dao;
import com.gym.model.Trainee;
import com.gym.service.TraineeService;
import com.gym.util.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger logger = LogManager.getLogger(TraineeServiceImpl.class);

    private Dao<Trainee> traineeDao;

    @Autowired
    public void setTraineeDao(Dao<Trainee> traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public Trainee createTrainee(Trainee trainee) {
        return UserUtils.setupNewUser(trainee, traineeDao, "trainee");
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        logger.info("Updating trainee with username: {}", trainee.getUsername());
        traineeDao.save(trainee.getUsername(), trainee);
        logger.info("Trainee updated successfully: {}", trainee.getUsername());
        return trainee;
    }

    @Override
    public void deleteTrainee(String username) {
        logger.info("Deleting trainee with username: {}", username);
        traineeDao.delete(username);
        logger.info("Trainee deleted successfully: {}", username);
    }

    @Override
    public Trainee getTraineeByUsername(String username) {
        logger.info("Retrieving trainee with username: {}", username);
        Trainee trainee = traineeDao.findById(username);
        if (trainee != null)
            logger.info("Trainee found: {}", username);
        else
            logger.info("Trainee not found: {}", username);

        return trainee;
    }

    @Override
    public List<Trainee> getAllTrainees() {
        logger.info("Retrieving all trainees");
        List<Trainee> trainees = traineeDao.findAll();
        logger.info("Retrieved {} trainees", trainees.size());
        return trainees;
    }
}