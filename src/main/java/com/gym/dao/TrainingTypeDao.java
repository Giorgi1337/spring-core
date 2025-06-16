package com.gym.dao;

import com.gym.model.TrainingType;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TrainingTypeDao extends AbstractDao<TrainingType, Long> {

    public TrainingTypeDao(SessionFactory sessionFactory) {
        super(sessionFactory, TrainingType.class);
    }

    public Optional<TrainingType> findByTrainingTypeName(String trainingTypeName) {
        return Optional.ofNullable(getSession().createQuery(
                        "FROM TrainingType tt WHERE tt.trainingTypeName = :trainingTypeName", TrainingType.class)
                .setParameter("trainingTypeName", trainingTypeName)
                .uniqueResult());
    }
}