package com.gym.service.impl;

import com.gym.dao.Dao;
import com.gym.model.Trainer;
import com.gym.service.TrainerService;
import com.gym.util.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LogManager.getLogger(TrainerServiceImpl.class);

    private Dao<Trainer> trainerDao;

    @Autowired
    public void setTrainerDao(Dao<Trainer> trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public Trainer createTrainer(Trainer trainer) {
        return UserUtils.setupNewUser(trainer, trainerDao, "trainer");
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        logger.info("Updating trainer with username: {}", trainer.getUsername());
        trainerDao.save(trainer.getUsername(), trainer);
        logger.info("Trainer updated successfully: {}", trainer.getUsername());
        return trainer;
    }

    @Override
    public Trainer getTrainerByUsername(String username) {
        logger.info("Retrieving trainer with username: {}", username);
        Trainer trainer = trainerDao.findById(username);
        if (trainer != null)
            logger.info("Trainer found: {}", username);
        else
            logger.info("Trainer not found: {}", username);

        return trainer;
    }

    @Override
    public List<Trainer> getAllTrainers() {
        logger.info("Retrieving all trainers");
        List<Trainer> trainers = trainerDao.findAll();
        logger.info("Retrieved {} trainers", trainers.size());
        return trainers;
    }
}
