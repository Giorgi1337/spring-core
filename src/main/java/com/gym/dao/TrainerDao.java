package com.gym.dao;

import com.gym.model.Trainer;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TrainerDao extends AbstractDao<Trainer> {

    public TrainerDao(SessionFactory sessionFactory) {
        super(sessionFactory, Trainer.class);
    }

    public Optional<Trainer> findByUsername(String username) {
        return Optional.ofNullable(getSession().createQuery(
                        "FROM Trainer t JOIN FETCH t.user WHERE t.user.username = :username", Trainer.class)
                .setParameter("username", username)
                .uniqueResult());
    }
}