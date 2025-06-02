package com.gym.service;

import com.gym.dao.Dao;
import com.gym.model.Trainer;
import com.gym.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerServiceImpl Tests")
public class TrainerServiceImplTest {

    @Mock
    private Dao<Trainer> trainerDao;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer sampleTrainer;

    @BeforeEach
    void setup() {
        sampleTrainer = Trainer.builder()
                .firstName("Jane")
                .lastName("Smith")
                .specialization("Cardio")
                .username("Jane.Smith")
                .build();
    }

    @Test
    @DisplayName("Create Trainer should generate username from first and last name")
    void createTrainerShouldGenerateUsernameFromFirstAndLastName() {
        when(trainerDao.findById("Jane.Smith")).thenReturn(null);

        Trainer created = trainerService.createTrainer(sampleTrainer);

        assertNotNull(created.getUsername());
        assertEquals("Jane.Smith", created.getUsername());
        assertNotNull(created.getPassword());
        assertTrue(created.isActive());

        verify(trainerDao).save(eq("Jane.Smith"), eq(created));
    }

    @Test
    @DisplayName("Create Trainer should add suffix to username if duplicate exists")
    void createTrainerShouldAddSuffixToUsernameIfDuplicateExists() {
        when(trainerDao.findById("Jane.Smith")).thenReturn(new Trainer());
        when(trainerDao.findById("Jane.Smith1")).thenReturn(null);

        Trainer created = trainerService.createTrainer(sampleTrainer);

        assertEquals("Jane.Smith1", created.getUsername());
        verify(trainerDao).save(eq("Jane.Smith1"), eq(created));
    }

    @Test
    @DisplayName("Update Trainer should save trainer with existing username")
    void updateTrainerShouldSaveTrainerWithExistingUsername() {
        Trainer existing = Trainer.builder()
                .username("Jane.Smith")
                .specialization("Strength")
                .build();

        Trainer updated = trainerService.updateTrainer(existing);

        assertEquals("Jane.Smith", updated.getUsername());
        verify(trainerDao).save("Jane.Smith", existing);
    }

    @Test
    @DisplayName("Get Trainer by username should return trainer if exists")
    void getTrainerByUsernameShouldReturnTrainerIfExists() {
        when(trainerDao.findById("Jane.Smith")).thenReturn(sampleTrainer);

        Trainer found = trainerService.getTrainerByUsername("Jane.Smith");

        assertNotNull(found);
        assertEquals("Jane.Smith", found.getUsername());
    }

    @Test
    @DisplayName("Get Trainer by username should return null if not found")
    void getTrainerByUsernameShouldReturnNullIfNotFound() {
        when(trainerDao.findById("Non.Existent")).thenReturn(null);

        Trainer found = trainerService.getTrainerByUsername("Non.Existent");

        assertNull(found);
    }

    @Test
    @DisplayName("Get all trainers should return all trainers with correct details")
    void getAllTrainersShouldReturnAllTrainers() {
        Trainer trainer1 = Trainer.builder()
                .firstName("Alice")
                .lastName("Anderson")
                .username("Alice.Anderson")
                .specialization("Yoga")
                .isActive(true)
                .build();

        Trainer trainer2 = Trainer.builder()
                .firstName("Bob")
                .lastName("Brown")
                .username("Bob.Brown")
                .specialization("Strength")
                .isActive(false)
                .build();

        List<Trainer> trainers = List.of(trainer1, trainer2);
        when(trainerDao.findAll()).thenReturn(trainers);

        List<Trainer> result = trainerService.getAllTrainers();

        assertEquals(2, result.size());

        Trainer first = result.get(0);
        assertEquals("Alice.Anderson", first.getUsername());
        assertEquals("Alice", first.getFirstName());
        assertEquals("Anderson", first.getLastName());
        assertEquals("Yoga", first.getSpecialization());
        assertTrue(first.isActive());

        Trainer second = result.get(1);
        assertEquals("Bob.Brown", second.getUsername());
        assertEquals("Bob", second.getFirstName());
        assertEquals("Brown", second.getLastName());
        assertEquals("Strength", second.getSpecialization());
        assertFalse(second.isActive());
    }
}