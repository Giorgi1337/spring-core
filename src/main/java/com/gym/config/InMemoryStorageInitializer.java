package com.gym.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.model.Trainee;
import com.gym.model.Trainer;
import com.gym.model.Training;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Component
public class InMemoryStorageInitializer implements BeanPostProcessor {

    private final Environment environment;
    private final ObjectMapper objectMapper;

    @Autowired
    public InMemoryStorageInitializer(Environment environment, ObjectMapper objectMapper) {
        this.environment = environment;
        this.objectMapper = objectMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        try {
            switch (beanName) {
                case "traineeStorage" -> {
                    Map<String, Trainee> storage = (Map<String, Trainee>) bean;
                    String file = environment.getProperty("init.trainee.file");
                    assert file != null;
                    Trainee[] trainees = objectMapper.readValue(new File(file), Trainee[].class);
                    for (Trainee t : trainees) {
                        storage.put(t.getUsername(), t);
                    }
                }
                case "trainerStorage" -> {
                    Map<String, Trainer> storage = (Map<String, Trainer>) bean;
                    String file = environment.getProperty("init.trainer.file");
                    assert file != null;
                    Trainer[] trainers = objectMapper.readValue(new File(file), Trainer[].class);
                    for (Trainer t : trainers) {
                        storage.put(t.getUsername(), t);
                    }
                }
                case "trainingStorage" -> {
                    Map<String, Training> storage = (Map<String, Training>) bean;
                    String file = environment.getProperty("init.training.file");
                    assert file != null;
                    Training[] trainings = objectMapper.readValue(new File(file), Training[].class);
                    for (Training t : trainings) {
                        String key = t.getTrainee().getUsername() + "_" + t.getTrainingName() + "_" + t.getTrainingDate();
                        storage.put(key, t);
                    }
                }
            }
        } catch (IOException e) {
            throw new BeanInitializationException("Failed to load initialization file", e);
        }

        return bean;
    }
}