package com.gym.util;

import com.gym.dao.Dao;
import com.gym.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class UserUtils {
    private static final Logger logger = LogManager.getLogger(UserUtils.class);
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;
    private static final Random random = new Random();

    public static String generateUniqueUsername(String firstName, String lastName, Dao dao) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int counter = 1;

        // Check if username already exists, if so add serial number
        while (dao.findById(username) != null) {
            username = baseUsername + counter;
            counter++;
        }

        logger.info("Generated unique username: {}", username);
        return username;
    }

    public static String generateRandomPassword() {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        String generatedPassword = password.toString();
        logger.info("Generated random password");
        return generatedPassword;
    }

    public static <T extends User> T setupNewUser(T user, Dao<T> dao, String userType) {
        logger.info("Creating new {}: {} {}", userType, user.getFirstName(), user.getLastName());

        String username = generateUniqueUsername(user.getFirstName(), user.getLastName(), dao);
        String password = generateRandomPassword();

        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        dao.save(user.getUsername(), user);
        logger.info("{} created successfully with username: {}", userType, user.getUsername());

        return user;
    }
}