package com.gym.service;

import com.gym.model.User;

public interface UserService {
    String generateUniqueUsername(String firstName, String lastName);
    String generateRandomPassword();
    void setupNewUser(User user);
    void updateUserDetails(User existingUser, User updatedUser);
}