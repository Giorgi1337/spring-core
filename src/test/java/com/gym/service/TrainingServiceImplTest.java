package com.gym.service;

import com.gym.dao.TraineeDao;
import com.gym.dao.TrainerDao;
import com.gym.dao.TrainingDao;
import com.gym.dao.TrainingTypeDao;
import com.gym.exception.AuthenticationException;
import com.gym.exception.TraineeNotFoundException;
import com.gym.exception.TrainerNotFoundException;
import com.gym.exception.TrainingTypeNotFoundException;
import com.gym.model.*;
import com.gym.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingServiceImpl Tests")
public class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;
    @Mock
    private TraineeDao traineeDao;
    @Mock
    private TrainerDao trainerDao;
    @Mock
    private TrainingTypeDao trainingTypeDao;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private User traineeUser;
    private User trainerUser;
    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;
    private Training training;

    @BeforeEach
    void setup() {
        traineeUser = User.builder()
                .username("trainee1")
                .password("traineePass")
                .firstName("Ana")
                .lastName("Smith")
                .isActive(true)
                .build();

        trainerUser = User.builder()
                .username("trainer1")
                .password("trainerPass")
                .firstName("Bob")
                .lastName("Strong")
                .isActive(true)
                .build();

        trainee = Trainee.builder()
                .user(traineeUser)
                .trainers(new HashSet<>())
                .trainings(new HashSet<>())
                .build();

        trainer = Trainer.builder()
                .user(trainerUser)
                .trainees(new HashSet<>())
                .trainings(new HashSet<>())
                .build();

        trainingType = TrainingType.builder()
                .id(1L)
                .trainingTypeName("Cardio")
                .build();

        training = Training.builder()
                .trainingName("Morning Cardio")
                .trainingDate(LocalDateTime.now())
                .trainingDuration(45)
                .trainingType(trainingType)
                .build();
    }

    @Test
    @DisplayName("Add training success and links all relationships")
    void addTrainingSuccess() {
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByTrainingTypeName("Cardio")).thenReturn(Optional.of(trainingType));

        Training result = trainingService.addTraining("trainee1", "traineePass", "trainer1", "trainerPass", training);

        verify(trainingDao).save(training);
        assertEquals(trainee, result.getTrainee());
        assertEquals(trainer, result.getTrainer());
        assertEquals(trainingType, result.getTrainingType());
        assertTrue(trainee.getTrainings().contains(training));
        assertTrue(trainer.getTrainings().contains(training));
        assertTrue(trainee.getTrainers().contains(trainer));
        assertTrue(trainer.getTrainees().contains(trainee));
    }

    @Test
    @DisplayName("Add training throws when trainee not found")
    void addTrainingTraineeNotFound() {
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class,
                () -> trainingService.addTraining("trainee1", "traineePass", "trainer1", "trainerPass", training));
    }

    @Test
    @DisplayName("Add training throws on invalid trainee password")
    void addTrainingInvalidTraineePassword() {
        traineeUser.setPassword("correctPass");
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        assertThrows(AuthenticationException.class,
                () -> trainingService.addTraining("trainee1", "wrongPass", "trainer1", "trainerPass", training));
    }

    @Test
    @DisplayName("Add training throws when trainer not found")
    void addTrainingTrainerNotFound() {
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class,
                () -> trainingService.addTraining("trainee1", "traineePass", "trainer1", "trainerPass", training));
    }

    @Test
    @DisplayName("Add training throws on invalid trainer password")
    void addTrainingInvalidTrainerPassword() {
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        trainerUser.setPassword("correctPass");

        assertThrows(AuthenticationException.class,
                () -> trainingService.addTraining("trainee1", "traineePass", "trainer1", "wrongPass", training));
    }

    @Test
    @DisplayName("Add training throws when training type not found")
    void addTrainingTrainingTypeNotFound() {
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByTrainingTypeName("Cardio")).thenReturn(Optional.empty());

        assertThrows(TrainingTypeNotFoundException.class,
                () -> trainingService.addTraining("trainee1", "traineePass", "trainer1", "trainerPass", training));
    }
}