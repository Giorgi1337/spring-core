package com.gym.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.model.Trainee;
import com.gym.model.Trainer;
import com.gym.model.Training;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

@Component
public class InMemoryStorageInitializer implements BeanPostProcessor, ApplicationContextAware {

    private static final Logger logger = LogManager.getLogger(InMemoryStorageInitializer.class);

    private final Environment environment;
    private final ObjectMapper objectMapper;
    private ApplicationContext applicationContext; // lazy fetch beans

    public InMemoryStorageInitializer(Environment environment) {
        this.environment = environment;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        try {
            switch (beanName) {
                case "traineeStorage" -> {
                    logger.info("Loading trainees from file...");
                    Map<String, Trainee> storage = (Map<String, Trainee>) bean;
                    String file = environment.getProperty("init.trainee.file");
                    assert file != null;
                    Trainee[] trainees = objectMapper.readValue(new File(file), Trainee[].class);

                    Map<String, Trainer> trainerStorage = applicationContext.getBean("trainerStorage", Map.class);

                    int generatedUsernames = 0;
                    int generatedPasswords = 0;

                    for (Trainee t : trainees) {
                        if (t.getUsername() == null || t.getUsername().isBlank()) {
                            String baseUsername = (t.getFirstName() + "." + t.getLastName()).toLowerCase();
                            String username = baseUsername;
                            int counter = 1;
                            while (storage.containsKey(username) || trainerStorage.containsKey(username)) {
                                username = baseUsername + counter++;
                            }
                            t.setUsername(username);
                            generatedUsernames++;
                        }
                        if (t.getPassword() == null || t.getPassword().isBlank()) {
                            t.setPassword(generateRandomPassword());
                            generatedPasswords++;
                        }
                        storage.put(t.getUsername(), t);
                    }
                    logger.info("Loaded {} trainees. Usernames generated: {}, passwords generated: {}",
                            trainees.length, generatedUsernames, generatedPasswords);
                }
                case "trainerStorage" -> {
                    logger.info("Loading trainers from file...");
                    Map<String, Trainer> storage = (Map<String, Trainer>) bean;
                    String file = environment.getProperty("init.trainer.file");
                    assert file != null;
                    Trainer[] trainers = objectMapper.readValue(new File(file), Trainer[].class);

                    Map<String, Trainee> traineeStorage = applicationContext.getBean("traineeStorage", Map.class);

                    int generatedUsernames = 0;
                    int generatedPasswords = 0;

                    for (Trainer t : trainers) {
                        if (t.getUsername() == null || t.getUsername().isBlank()) {
                            String baseUsername = (t.getFirstName() + "." + t.getLastName()).toLowerCase();
                            String username = baseUsername;
                            int counter = 1;
                            while (traineeStorage.containsKey(username) || storage.containsKey(username)) {
                                username = baseUsername + counter++;
                            }
                            t.setUsername(username);
                            generatedUsernames++;
                        }
                        if (t.getPassword() == null || t.getPassword().isBlank()) {
                            t.setPassword(generateRandomPassword());
                            generatedPasswords++;
                        }
                        storage.put(t.getUsername(), t);
                    }
                    logger.info("Loaded {} trainers. Usernames generated: {}, passwords generated: {}",
                            trainers.length, generatedUsernames, generatedPasswords);
                }
                case "trainingStorage" -> {
                    logger.info("Loading trainings from file...");
                    Map<String, Training> storage = (Map<String, Training>) bean;
                    String file = environment.getProperty("init.training.file");
                    assert file != null;
                    Training[] trainings = objectMapper.readValue(new File(file), Training[].class);

                    for (Training t : trainings) {
                        String key = t.getTrainee().getUsername() + "_" + t.getTrainingName() + "_" + t.getTrainingDate();
                        storage.put(key, t);
                    }
                    logger.info("Loaded {} trainings", trainings.length);
                }
                default -> {}
            }
        } catch (IOException e) {
            logger.error("Failed to load initialization file for bean '{}'", beanName, e);
            throw new BeanInitializationException("Failed to load initialization file for bean: " + beanName, e);
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;
    private static final Random random = new Random();

    private String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }
}