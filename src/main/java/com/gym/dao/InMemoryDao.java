package com.gym.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryDao<T> implements Dao<T> {
    private final Map<String, T> storage;

    public InMemoryDao(Map<String, T> storage) {
        this.storage = storage;
    }

    @Override
    public void save(String id, T entity) {
        storage.put(id, entity);
    }

    @Override
    public T findById(String id) {
        return storage.get(id);
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(String id) {
        storage.remove(id);
    }
}