package com.gym.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.dao.Dao;
import com.gym.dao.InMemoryDao;
import com.gym.model.Trainee;
import com.gym.model.Trainer;
import com.gym.model.Training;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "com.gym")
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public Map<String, Trainee> traineeStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, Trainer> trainerStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, Training> trainingStorage() {
        return new HashMap<>();
    }

    @Bean
    public Dao<Trainee> traineeDao(Map<String, Trainee> traineeStorage) {
        return new InMemoryDao<>(traineeStorage);
    }

    @Bean
    public Dao<Trainer> trainerDao(Map<String, Trainer> trainerStorage) {
        return new InMemoryDao<>(trainerStorage);
    }

    @Bean
    public Dao<Training> trainingDao(Map<String, Training> trainingStorage) {
        return new InMemoryDao<>(trainingStorage);
    }

}