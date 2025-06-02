package com.gym.service;

import com.gym.dao.Dao;
import com.gym.model.Trainee;
import com.gym.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraineeServiceImpl Tests")
public class TraineeServiceImplTest {

    @Mock
    private Dao<Trainee> traineeDao;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    @DisplayName("Should create trainee with generated username and password")
    void createTraineeGeneratesUsernameAndPassword() {
        LocalDate dob = LocalDate.of(1990, 5, 15);
        Trainee input = Trainee.builder()
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(dob)
                .address("123 Main St")
                .build();

        when(traineeDao.findById(anyString())).thenReturn(null);

        Trainee created = traineeService.createTrainee(input);

        assertNotNull(created.getUsername());
        assertTrue(created.getUsername().startsWith("John.Doe"));
        assertNotNull(created.getPassword());
        assertEquals(10, created.getPassword().length());
        assertTrue(created.isActive());

        verify(traineeDao).save(eq(created.getUsername()), eq(created));
    }

    @Test
    @DisplayName("Should update trainee in DAO and return updated trainee")
    void updateTraineeSavesToDao() {
        Trainee trainee = Trainee.builder()
                .username("john.doe")
                .firstName("John")
                .lastName("Doe")
                .build();

        Trainee updated = traineeService.updateTrainee(trainee);

        verify(traineeDao).save("john.doe", trainee);
        assertEquals(trainee, updated);
    }

    @Test
    @DisplayName("Should delete trainee by username")
    void deleteTraineeByUsername() {
        String username = "john.doe";
        traineeService.deleteTrainee(username);
        verify(traineeDao).delete(username);
    }

    @Test
    @DisplayName("Should retrieve existing trainee by username")
    void getExistingTraineeByUsername() {
        String username = "john.doe";
        Trainee trainee = Trainee.builder().username(username).build();
        when(traineeDao.findById(username)).thenReturn(trainee);

        Trainee result = traineeService.getTraineeByUsername(username);

        assertEquals(trainee, result);
        verify(traineeDao).findById(username);
    }

    @Test
    @DisplayName("Should return null when trainee not found")
    void getNonExistingTraineeReturnsNull() {
        when(traineeDao.findById("unknown")).thenReturn(null);

        Trainee result = traineeService.getTraineeByUsername("unknown");

        assertNull(result);
        verify(traineeDao).findById("unknown");
    }

    @Test
    @DisplayName("Should return list of all trainees")
    void getAllTraineesReturnsList() {
        List<Trainee> trainees = Arrays.asList(
                Trainee.builder().username("john.doe").build(),
                Trainee.builder().username("jane.smith").build()
        );

        when(traineeDao.findAll()).thenReturn(trainees);

        List<Trainee> result = traineeService.getAllTrainees();

        assertEquals(2, result.size());
        assertEquals("john.doe", result.get(0).getUsername());
        assertEquals("jane.smith", result.get(1).getUsername());
        verify(traineeDao).findAll();
    }
}