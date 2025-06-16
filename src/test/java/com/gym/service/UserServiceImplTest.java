package com.gym.service;

import com.gym.dao.UserDao;
import com.gym.model.User;
import com.gym.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .password("oldPassword")
                .isActive(true)
                .build();
    }

    @Test
    void generatesUniqueUsernameWhenNoConflict() {
        when(userDao.existsByUsername("john.doe")).thenReturn(false);
        String result = userService.generateUniqueUsername("John", "Doe");
        assertEquals("john.doe", result);
    }

    @Test
    void appendsCounterOnUsernameConflict() {
        when(userDao.existsByUsername("john.doe")).thenReturn(true);
        when(userDao.existsByUsername("john.doe1")).thenReturn(true);
        when(userDao.existsByUsername("john.doe2")).thenReturn(false);

        String result = userService.generateUniqueUsername("John", "Doe");
        assertEquals("john.doe2", result);
    }

    @Test
    void trimsAndLowercasesNames() {
        when(userDao.existsByUsername("john.doe")).thenReturn(false);
        String result = userService.generateUniqueUsername("  JoHn  ", "  DoE ");
        assertEquals("john.doe", result);
    }

    @Test
    void handlesShortOrNumericNames() {
        when(userDao.existsByUsername("a.b")).thenReturn(false);
        assertEquals("a.b", userService.generateUniqueUsername("A", "B"));

        when(userDao.existsByUsername("john123.doe456")).thenReturn(false);
        assertEquals("john123.doe456", userService.generateUniqueUsername("John123", "Doe456"));
    }

    @Test
    void generatesValidRandomPassword() {
        String password = userService.generateRandomPassword();
        assertEquals(10, password.length());
        assertTrue(password.matches("[A-Za-z0-9]+"));
    }

    @Test
    void generatesDifferentPasswords() {
        assertNotEquals(userService.generateRandomPassword(), userService.generateRandomPassword());
    }

    @Test
    void setsUpNewUser() {
        User newUser = User.builder().firstName("Jane").lastName("Smith").build();
        when(userDao.existsByUsername("jane.smith")).thenReturn(false);

        userService.setupNewUser(newUser);

        assertEquals("jane.smith", newUser.getUsername());
        assertEquals(10, newUser.getPassword().length());
        assertTrue(newUser.getIsActive());
    }

    @Test
    void handlesUsernameConflictInSetup() {
        User newUser = User.builder().firstName("Jane").lastName("Smith").build();
        when(userDao.existsByUsername("jane.smith")).thenReturn(true);
        when(userDao.existsByUsername("jane.smith1")).thenReturn(false);

        userService.setupNewUser(newUser);

        assertEquals("jane.smith1", newUser.getUsername());
    }

    @Test
    void updatesUserFieldsExceptPassword() {
        User update = User.builder()
                .firstName("  Jane  ")
                .lastName("  Smith  ")
                .isActive(false)
                .build();
        when(userDao.existsByUsername("jane.smith")).thenReturn(false);

        userService.updateUserDetails(existingUser, update);

        assertEquals("Jane", existingUser.getFirstName());
        assertEquals("Smith", existingUser.getLastName());
        assertEquals("jane.smith", existingUser.getUsername());
        assertFalse(existingUser.getIsActive());
        assertEquals("oldPassword", existingUser.getPassword());
    }

    @Test
    void handlesUsernameConflictDuringUpdate() {
        User update = User.builder().firstName("Jane").lastName("Smith").build();
        when(userDao.existsByUsername("jane.smith")).thenReturn(true);
        when(userDao.existsByUsername("jane.smith1")).thenReturn(false);

        userService.updateUserDetails(existingUser, update);

        assertEquals("jane.smith1", existingUser.getUsername());
    }

    @Test
    void skipsUsernameChangeIfSameName() {
        User update = User.builder().firstName("John").lastName("Doe").isActive(false).build();
        when(userDao.existsByUsername("john.doe")).thenReturn(false);

        userService.updateUserDetails(existingUser, update);

        assertEquals("john.doe", existingUser.getUsername());
        assertFalse(existingUser.getIsActive());
    }
}