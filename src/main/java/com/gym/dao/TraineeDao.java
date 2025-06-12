package com.gym.dao;

import com.gym.model.Trainee;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TraineeDao extends AbstractDao<Trainee> {

    public TraineeDao(SessionFactory sessionFactory) {
        super(sessionFactory, Trainee.class);
    }

    public Optional<Trainee> findByUsername(String username) {
        return Optional.ofNullable(getSession().createQuery(
                        "FROM Trainee t JOIN FETCH t.user WHERE t.user.username = :username", Trainee.class)
                .setParameter("username", username)
                .uniqueResult());
    }
}