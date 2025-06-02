package com.gym.service.impl;

import com.gym.dao.Dao;
import com.gym.model.Training;
import com.gym.service.TrainingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger logger = LogManager.getLogger(TrainingServiceImpl.class);

    private Dao<Training> trainingDao;

    @Autowired
    public void setTrainingDao(Dao<Training> trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Override
    public Training createTraining(Training training) {
        logger.info("Creating new training: {}", training.getTrainingName());
        String trainingId = generateTrainingId(training);
        trainingDao.save(trainingId, training);
        logger.info("Training created successfully with ID: {}", trainingId);
        return training;
    }

    @Override
    public Training selectTrainingById(String trainingId) {
        logger.info("Selecting training with ID: {}", trainingId);
        Training training = trainingDao.findById(trainingId);

        if (training != null)
            logger.info("Training found: {}", trainingId);
        else
            logger.info("Training not found: {}", trainingId);

        return training;
    }

    @Override
    public List<Training> selectAllTrainings() {
        logger.info("Selecting all trainings");
        List<Training> trainings = trainingDao.findAll();
        logger.info("Selected {} trainings", trainings.size());
        return trainings;
    }

    private String generateTrainingId(Training training) {
        return String.format("Trainee:%s_Trainer:%s_Date:%s_Training:%s_%d",
                training.getTrainee().getUsername(),
                training.getTrainer().getUsername(),
                training.getTrainingDate().toString(),
                training.getTrainingName().replaceAll("\\s+", "_"),
                System.currentTimeMillis());
    }
}