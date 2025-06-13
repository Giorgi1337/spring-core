package com.gym.dao;

import com.gym.model.Trainee;
import com.gym.model.Trainer;
import jakarta.persistence.criteria.*;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeDao extends AbstractDao<Trainee, Long> {

    public TraineeDao(SessionFactory sessionFactory) {
        super(sessionFactory, Trainee.class);
    }

    public Optional<Trainee> findByUsername(String username) {
        return Optional.ofNullable(getSession().createQuery(
                        "FROM Trainee t JOIN FETCH t.user WHERE t.user.username = :username", Trainee.class)
                .setParameter("username", username)
                .uniqueResult());
    }

    public List<Trainer> findUnassignedTrainersCriteria(String traineeUsername) {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Trainer> query = cb.createQuery(Trainer.class);
        Root<Trainer> root = query.from(Trainer.class);

        root.fetch("user");

        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Trainee> traineeRoot = subquery.from(Trainee.class);
        Join<Trainee, Trainer> assignedTrainers = traineeRoot.join("trainers");

        subquery.select(assignedTrainers.get("id"))
                .where(cb.equal(traineeRoot.get("user").get("username"), traineeUsername));

        Predicate isActive = cb.isTrue(root.get("user").get("isActive"));
        Predicate notAssigned = cb.not(root.get("id").in(subquery));

        query.select(root).where(cb.and(isActive, notAssigned));
        query.distinct(true);

        return getSession().createQuery(query).getResultList();
    }
}