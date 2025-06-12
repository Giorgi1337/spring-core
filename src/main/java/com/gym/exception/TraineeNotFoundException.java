package com.gym.exception;

public class TraineeNotFoundException extends RuntimeException {
    public TraineeNotFoundException(String message) {
        super(message);
    }
}