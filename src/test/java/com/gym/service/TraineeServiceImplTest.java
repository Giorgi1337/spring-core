package com.gym.service;

import com.gym.dao.TraineeDao;
import com.gym.dao.TrainerDao;
import com.gym.dao.TrainingDao;
import com.gym.exception.AuthenticationException;
import com.gym.model.Trainee;
import com.gym.model.Trainer;
import com.gym.model.Training;
import com.gym.model.User;
import com.gym.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private TraineeDao traineeDao;
    @Mock
    private TrainerDao trainerDao;
    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private final String username = "john.doe";
    private final String password = "password123";

    private User user;
    private Trainee trainee;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username(username)
                .password(password)
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .build();

        trainee = Trainee.builder()
                .user(user)
                .address("123 Street")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .build();
    }

    @Test
    void shouldAuthenticateTraineeSuccessfully() {
        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.authenticateTrainee(username, password);

        assertEquals(username, result.getUser().getUsername());
        verify(traineeDao).findByUsername(username);
    }

    @Test
    void shouldThrowWhenAuthenticationFails() {
        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));

        assertThrows(AuthenticationException.class, () ->
                traineeService.authenticateTrainee(username, "wrongPass"));
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));

        traineeService.changePassword(username, password, "newPass");

        assertEquals("newPass", trainee.getUser().getPassword());
        verify(traineeDao).update(trainee);
    }

    @Test
    void shouldUpdateTraineeSuccessfully() {
        User updatedUser = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .isActive(false)
                .build();

        Trainee updated = Trainee.builder()
                .user(updatedUser)
                .address("New Address")
                .dateOfBirth(LocalDate.of(1990, 5, 5))
                .build();

        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.updateTrainee(username, password, updated);

        assertEquals("New Address", result.getAddress());
        assertEquals(LocalDate.of(1990, 5, 5), result.getDateOfBirth());
        verify(userService).updateUserDetails(user, updatedUser);
        verify(traineeDao).update(trainee);
    }

    @Test
    void shouldToggleTraineeActiveStatus() {
        user.setIsActive(true);
        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.toggleTraineeActiveStatus(username, password);

        assertFalse(result.getUser().getIsActive());
        verify(traineeDao).update(trainee);
    }

    @Test
    void shouldDeleteTraineeSuccessfully() {
        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));

        Trainee deleted = traineeService.deleteTrainee(username, password);

        assertEquals(username, deleted.getUser().getUsername());
        verify(traineeDao).delete(trainee);
    }

    @Test
    void shouldReturnTraineeTrainings() {
        List<Training> trainings = List.of(new Training());
        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(trainingDao.findByTraineeUsernameAndCriteria(eq(username), any(), any(), any(), any()))
                .thenReturn(trainings);

        List<Training> result = traineeService.getTraineeTrainingsList(username, password, null, null, null, null);

        assertEquals(1, result.size());
        verify(trainingDao).findByTraineeUsernameAndCriteria(eq(username), any(), any(), any(), any());
    }

    @Test
    void shouldReturnUnassignedTrainers() {
        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(traineeDao.findUnassignedTrainersCriteria(username)).thenReturn(List.of(new Trainer()));

        Set<Trainer> result = traineeService.getUnassignedTrainers(username, password);

        assertEquals(1, result.size());
        verify(traineeDao).findUnassignedTrainersCriteria(username);
    }
}