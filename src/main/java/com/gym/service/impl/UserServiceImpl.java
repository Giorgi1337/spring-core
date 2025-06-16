package com.gym.service.impl;

import com.gym.dao.UserDao;
import com.gym.model.User;
import com.gym.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserDao userDao;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;
    private static final SecureRandom random = new SecureRandom();

    @Override
    public String generateUniqueUsername(String firstName, String lastName) {
        String base = firstName.trim().toLowerCase() + "." + lastName.trim().toLowerCase();
        String username = base;
        int counter = 1;

        while (userDao.existsByUsername(username)) {
            username = base + counter;
            counter++;
        }

        logger.info("[USER_SERVICE] [USERNAME_GENERATION] Generated unique username: {}", username);
        return username;
    }

    @Override
    public String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        logger.info("[USER_SERVICE] [PASSWORD_GENERATION] Random password generated");
        return password.toString();
    }

    @Override
    public void setupNewUser(User user) {
        String username = generateUniqueUsername(user.getFirstName(), user.getLastName());
        String password = generateRandomPassword();

        user.setUsername(username);
        user.setPassword(password);
        user.setIsActive(true);

        logger.info("[USER_SERVICE] [SETUP] Initialized user: username={}, fullName='{} {}'",
                username, user.getFirstName(), user.getLastName());
    }

    @Override
    public void updateUserDetails(User existingUser, User updatedUser) {
        String first = updatedUser.getFirstName().trim();
        String last = updatedUser.getLastName().trim();
        String newUsername = generateUniqueUsername(first, last);

        existingUser.setFirstName(first);
        existingUser.setLastName(last);
        existingUser.setUsername(newUsername);
        existingUser.setIsActive(updatedUser.getIsActive());

        logger.info("[USER_SERVICE] [UPDATE] Updated user details: newUsername={}", newUsername);
    }
}