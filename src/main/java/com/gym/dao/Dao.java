package com.gym.dao;

import java.util.List;

public interface Dao<T> {
    void save(String id, T entity);
    T findById(String id);
    List<T> findAll();
    void delete(String id);
}