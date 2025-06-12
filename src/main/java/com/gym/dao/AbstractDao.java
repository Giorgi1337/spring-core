package com.gym.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public abstract class AbstractDao<T> {

    protected final SessionFactory sessionFactory;
    private final Class<T> clazz;

    public AbstractDao(SessionFactory sessionFactory, Class<T> clazz) {
        this.sessionFactory = sessionFactory;
        this.clazz = clazz;
    }

    public T findById(Long id) {
        return getSession().find(clazz, id);
    }

    public List<T> findAll() {
        return getSession().createQuery("from " + clazz.getSimpleName(), clazz).list();
    }

    public void save(T entity) {
        getSession().persist(entity);
    }

    public void update(T entity) {
        getSession().merge(entity);
    }

    public void delete(T entity) {
        getSession().remove(entity);
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}