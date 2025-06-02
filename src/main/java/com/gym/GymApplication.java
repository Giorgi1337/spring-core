package com.gym;
import com.gym.config.AppConfig;
import com.gym.facade.GymFacade;
import com.gym.model.Trainee;
import com.gym.model.Trainer;
import com.gym.model.Training;
import com.gym.model.TrainingType;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
public class GymApplication {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            GymFacade gymFacade = context.getBean(GymFacade.class);
            Trainee trainee = Trainee.builder()
                    .firstName("Anna")
                    .lastName("Banna")
                    .dateOfBirth(LocalDate.of(1982, 1, 8))
                    .address("404 Cedar Ct, Lakeview")
                    .isActive(true)
                    .build();
            Trainee createdTrainee = gymFacade.registerTrainee(trainee);
            System.out.println("Created Trainee: " + createdTrainee);
            Trainer trainer = Trainer.builder()
                    .firstName("Jane")
                    .lastName("Doe")
                    .specialization("Yoga")
                    .isActive(true)
                    .build();
            Trainer createdTrainer = gymFacade.registerTrainer(trainer);
            System.out.println("Created Trainer: " + createdTrainer);
            Training training = Training.builder()
                    .trainingName("Morning Yoga")
                    .trainingType(TrainingType.YOGA)
                    .trainingDate(LocalDate.now().plusDays(2))
                    .trainingDuration(60)
                    .trainee(createdTrainee)
                    .trainer(createdTrainer)
                    .build();
            Training scheduledTraining = gymFacade.scheduleTraining(training);
            System.out.println("Scheduled Training: " + scheduledTraining);
            createdTrainee.setAddress("New Address 123");
            Trainee updatedTrainee = gymFacade.updateTraineeProfile(createdTrainee);
            System.out.println("Updated Trainee: " + updatedTrainee);
            System.out.println("All Trainees:");
            gymFacade.getAllTraineesList().forEach(System.out::println);
            gymFacade.deleteTraineeAccount(createdTrainee.getUsername());
            System.out.println("Deleted trainee: " + createdTrainee.getUsername());
        }
    }
}