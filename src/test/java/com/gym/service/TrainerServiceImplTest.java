package com.gym.service;

import com.gym.dao.TrainerDao;
import com.gym.exception.AuthenticationException;
import com.gym.exception.TrainerNotFoundException;
import com.gym.model.Trainer;
import com.gym.model.User;
import com.gym.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerServiceImpl Tests")
public class TrainerServiceImplTest {

    @Mock
    private TrainerDao trainerDao;
    @Mock
    private UserService userService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer trainer;
    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .username("trainer1")
                .password("pass123")
                .isActive(true)
                .firstName("John")
                .lastName("Doe")
                .build();

        trainer = Trainer.builder()
                .user(user)
                .specialization("Strength")
                .build();
    }

    @Test
    @DisplayName("Create trainer sets up user and saves trainer")
    void createTrainerSuccess() {
        trainerService.createTrainer(trainer);

        verify(userService).setupNewUser(user);
        verify(trainerDao).save(trainer);
    }

    @Test
    @DisplayName("Authenticate trainer success")
    void authenticateTrainerSuccess() {
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.authenticateTrainer("trainer1", "pass123");

        assertEquals(trainer, result);
    }

    @Test
    @DisplayName("Authenticate trainer throws if not found")
    void authenticateTrainerNotFound() {
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class,
                () -> trainerService.authenticateTrainer("trainer1", "pass123"));
    }

    @Test
    @DisplayName("Authenticate trainer throws if password incorrect")
    void authenticateTrainerInvalidPassword() {
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        assertThrows(AuthenticationException.class,
                () -> trainerService.authenticateTrainer("trainer1", "wrong"));
    }

    @Test
    @DisplayName("Change password success")
    void changePasswordSuccess() {
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        trainerService.changePassword("trainer1", "pass123", "newPass");

        assertEquals("newPass", trainer.getUser().getPassword());
        verify(trainerDao).update(trainer);
    }

    @Test
    @DisplayName("Update trainer details")
    void updateTrainerSuccess() {
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        Trainer update = Trainer.builder()
                .user(User.builder().firstName("New").lastName("Name").isActive(false).build())
                .specialization("Cardio")
                .build();

        Trainer result = trainerService.updateTrainer("trainer1", "pass123", update);

        verify(userService).updateUserDetails(user, update.getUser());
        verify(trainerDao).update(trainer);
        assertEquals("Cardio", result.getSpecialization());
    }

    @Test
    @DisplayName("Toggle trainer active status")
    void toggleTrainerActiveStatus() {
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        trainer.setUser(User.builder().isActive(true).password("pass123").username("trainer1").build());
        Trainer result = trainerService.toggleTrainerActiveStatus("trainer1", "pass123");

        assertFalse(result.getUser().getIsActive());
        verify(trainerDao).update(trainer);
    }

    @Test
    @DisplayName("Delete trainer")
    void deleteTrainer() {
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        Trainer deleted = trainerService.deleteTrainer("trainer1", "pass123");

        verify(trainerDao).delete(trainer);
        assertEquals(trainer, deleted);
    }

    @Test
    @DisplayName("Get trainings by criteria")
    void getTrainerTrainingsList() {
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainerDao.findTrainerTrainingsWithCriteria("trainer1", null, null, null))
                .thenReturn(Collections.emptyList());

        var result = trainerService.getTrainerTrainingsList("trainer1", "pass123", null, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}