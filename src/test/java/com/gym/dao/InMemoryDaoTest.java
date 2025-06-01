package com.gym.dao;

import com.gym.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;

@DisplayName("InMemoryDao Unit Tests for Trainee Entity")
public class InMemoryDaoTest {

    private InMemoryDao<Trainee> dao;
    private Map<String, Trainee> storage;
    private Trainee testTrainee;

    @BeforeEach
    void setUp() {
        storage = new HashMap<>();
        dao = new InMemoryDao<>(storage);
        testTrainee = Trainee.builder()
                .firstName("John")
                .lastName("Doe")
                .username("John.Doe")
                .build();
    }

    @Test
    @DisplayName("Should save a trainee to in-memory storage")
    void testSave() {
        dao.save("John.Doe", testTrainee);
        assertEquals(testTrainee, storage.get("John.Doe"));
    }

    @Test
    @DisplayName("Should find a trainee by ID")
    void testFindById() {
        storage.put("John.Doe", testTrainee);
        Trainee result = dao.findById("John.Doe");
        assertEquals(testTrainee, result);
    }

    @Test
    @DisplayName("Should return null when trainee not found by ID")
    void testFindById_NotFound() {
        Trainee result = dao.findById("nonexistent");
        assertNull(result);
    }

    @Test
    @DisplayName("Should return all trainees from storage")
    void testFindAll() {
        storage.put("John.Doe", testTrainee);
        List<Trainee> result = dao.findAll();
        assertEquals(1, result.size());
        assertTrue(result.contains(testTrainee));
    }

    @Test
    @DisplayName("Should delete a trainee from storage by ID")
    void testDelete() {
        storage.put("John.Doe", testTrainee);
        dao.delete("John.Doe");
        assertNull(storage.get("John.Doe"));
    }
}