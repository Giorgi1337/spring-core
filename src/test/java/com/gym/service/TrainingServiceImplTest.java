package com.gym.service;

import com.gym.dao.Dao;
import com.gym.model.Trainer;
import com.gym.model.Trainee;
import com.gym.model.Training;
import com.gym.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceImplTest {

    @Mock
    private Dao<Training> trainingDao;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Trainee sampleTrainee;
    private Trainer sampleTrainer;
    private Training sampleTraining;

    @BeforeEach
    void setup() {
        sampleTrainee = Trainee.builder()
                .username("John.Doe")
                .firstName("John")
                .lastName("Doe")
                .build();

        sampleTrainer = Trainer.builder()
                .username("Jane.Smith")
                .firstName("Jane")
                .lastName("Smith")
                .build();

        sampleTraining = Training.builder()
                .trainee(sampleTrainee)
                .trainer(sampleTrainer)
                .trainingName("Morning Cardio")
                .trainingDate(LocalDate.of(2025, 6, 3))
                .trainingDuration(60)
                .build();
    }

    @Test
    @DisplayName("Create training should save training with generated ID and return the training")
    void createTrainingShouldSaveTrainingWithGeneratedId() {
        Training created = trainingService.createTraining(sampleTraining);

        // Capture argument used for save key
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(trainingDao).save(idCaptor.capture(), eq(sampleTraining));

        String expectedId = "Trainee:John.Doe_Trainer:Jane.Smith_Date:2025-06-03_Training:Morning_Cardio_Duration:60min";
        assertEquals(expectedId, idCaptor.getValue());
        assertSame(sampleTraining, created);
    }

    @Test
    @DisplayName("Select training by ID should return training if found")
    void selectTrainingByIdShouldReturnTrainingIfFound() {
        String trainingId = "someTrainingId";
        when(trainingDao.findById(trainingId)).thenReturn(sampleTraining);

        Training found = trainingService.selectTrainingById(trainingId);

        assertNotNull(found);
        assertEquals(sampleTraining, found);
        verify(trainingDao).findById(trainingId);
    }

    @Test
    @DisplayName("Select training by ID should return null if training not found")
    void selectTrainingByIdShouldReturnNullIfNotFound() {
        String trainingId = "nonexistentId";
        when(trainingDao.findById(trainingId)).thenReturn(null);

        Training found = trainingService.selectTrainingById(trainingId);

        assertNull(found);
        verify(trainingDao).findById(trainingId);
    }

    @Test
    @DisplayName("Select all trainings should return list of all trainings")
    void selectAllTrainingsShouldReturnAllTrainings() {
        List<Training> trainings = List.of(
                sampleTraining,
                Training.builder().trainingName("Evening Strength").build()
        );
        when(trainingDao.findAll()).thenReturn(trainings);

        List<Training> result = trainingService.selectAllTrainings();

        assertEquals(2, result.size());
        assertTrue(result.contains(sampleTraining));
        verify(trainingDao).findAll();
    }
}