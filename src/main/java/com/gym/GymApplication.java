package com.gym;
import com.gym.config.AppConfig;
import com.gym.model.*;
import com.gym.service.TraineeService;
import com.gym.service.TrainerService;
import com.gym.service.TrainingService;
import com.gym.service.TrainingTypeService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class GymApplication {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(AppConfig.class);
        TrainingTypeService trainingTypeService = context.getBean(TrainingTypeService.class);
        TrainingService trainingService = context.getBean(TrainingService.class);
        TrainerService trainerService = context.getBean(TrainerService.class);
        TraineeService traineeService = context.getBean(TraineeService.class);

        // Try to create or fetch from DB
        TrainingType cardio = trainingTypeService.getTrainingTypeByName("Cardio").orElseThrow();
        TrainingType strength = trainingTypeService.getTrainingTypeByName("Strength").orElseThrow();

        // Create Trainer/Trainee
        Trainer trainer = Trainer.builder()
                .specialization("Weightlifting")
                .user(User.builder()
                        .firstName("Anna")
                        .lastName("Banna")
                        .build())
                .build();
        Trainee trainee = Trainee.builder()
                .user(User.builder()
                        .firstName("John")
                        .lastName("Cena")
                        .build())
                .address("Qwerty 123")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .build();

        Trainee createdTrainee = traineeService.createTrainee(trainee);
        Trainer createdTrainer = trainerService.createTrainer(trainer);

        String trainerUsername = createdTrainer.getUser().getUsername();
        String trainerPassword = createdTrainer.getUser().getPassword();
        String traineeUsername = createdTrainee.getUser().getUsername();
        String traineePassword = createdTrainee.getUser().getPassword();

        // Update Profiles and get updated entities with new usernames
        Trainer updatedTrainer = Trainer.builder()
                .specialization("Crossfit")
                .user(User.builder()
                        .firstName("Annabelle")
                        .lastName("Banna")
                        .isActive(false)
                        .build())
                .build();
        Trainer updatedTrainerEntity = trainerService.updateTrainer(trainerUsername, trainerPassword, updatedTrainer);
        String updatedTrainerUsername = updatedTrainerEntity.getUser().getUsername();

        Trainee updatedTrainee = Trainee.builder()
                .address("New Address 456")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .user(User.builder()
                        .firstName("Max")
                        .lastName("Verstappen")
                        .isActive(true)
                        .build())
                .build();
        Trainee updatedTraineeEntity = traineeService.updateTrainee(traineeUsername, traineePassword, updatedTrainee);
        String updatedTraineeUsername = updatedTraineeEntity.getUser().getUsername();

        // Change Passwords using updated usernames and old passwords
        trainerService.changePassword(updatedTrainerUsername, trainerPassword, "newStrongPass1");
        traineeService.changePassword(updatedTraineeUsername, traineePassword, "Hamilton007");

        // Toggle active status for trainee and trainer
        traineeService.toggleTraineeActiveStatus(updatedTraineeUsername, "Hamilton007");
        trainerService.toggleTrainerActiveStatus(updatedTrainerUsername, "newStrongPass1");

        traineeService.updateTraineeTrainersList(updatedTraineeUsername, "Hamilton007", List.of(updatedTrainerUsername));

        // Add a Training
        Training newTraining = Training.builder()
                .trainingName("Morning Cardio Blast")
                .trainingDate(LocalDateTime.now().plusDays(5).plusHours(3).plusMinutes(5))
                .trainingDuration(60)
                .trainingType(cardio) // Use the 'cardio' TrainingType
                .build();

        // Add the training session, associating the trainee, trainer, and training type
        Training createdTraining = trainingService.addTraining(
                updatedTraineeUsername,
                "Hamilton007",
                updatedTrainerUsername,
                "newStrongPass1",
                newTraining
        );
    }
}