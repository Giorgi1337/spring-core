package com.gym.dao;

import com.gym.model.Trainee;
import com.gym.model.Trainer;
import com.gym.model.Training;
import com.gym.model.User;
import jakarta.persistence.criteria.*;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDao extends AbstractDao<Trainer, Long> {

    public TrainerDao(SessionFactory sessionFactory) {
        super(sessionFactory, Trainer.class);
    }

    public Optional<Trainer> findByUsername(String username) {
        Query<Trainer> query = getSession().createQuery(
                        "FROM Trainer t JOIN FETCH t.user WHERE t.user.username = :username", Trainer.class)
                .setParameter("username", username);
        return query.uniqueResultOptional();
    }

    public Optional<Trainer> findByUsernameWithTrainings(String username) {
        Query<Trainer> query = getSession().createQuery(
                        "FROM Trainer t JOIN FETCH t.user LEFT JOIN FETCH t.trainings WHERE t.user.username = :username", Trainer.class)
                .setParameter("username", username);
        return query.uniqueResultOptional();
    }

    public List<Training> findTrainerTrainingsWithCriteria(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> root = query.from(Training.class);

        Fetch<Training, Trainer> trainerFetch = root.fetch("trainer", JoinType.LEFT);
        trainerFetch.fetch("user", JoinType.LEFT);

        Fetch<Training, Trainee> traineeFetch = root.fetch("trainee", JoinType.LEFT);
        traineeFetch.fetch("user", JoinType.LEFT);

        root.fetch("trainingType", JoinType.LEFT);

        Join<Training, Trainer> trainerJoin = root.join("trainer", JoinType.LEFT);
        Join<Trainer, User> trainerUserJoin = trainerJoin.join("user", JoinType.LEFT);

        Join<Training, Trainee> traineeJoin = root.join("trainee", JoinType.LEFT);
        Join<Trainee, User> traineeUserJoin = traineeJoin.join("user", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(trainerUserJoin.get("username"), username));
        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("trainingDate"), toDate));
        }
        if (traineeName != null && !traineeName.isBlank()) {
            Expression<String> traineeFullName = cb.concat(
                    cb.concat(cb.lower(traineeUserJoin.get("firstName")), " "),
                    cb.lower(traineeUserJoin.get("lastName"))
            );
            predicates.add(cb.like(traineeFullName, "%" + traineeName.toLowerCase() + "%"));
        }

        query.select(root).where(predicates.toArray(new Predicate[0]));
        query.distinct(true);

        return getSession().createQuery(query).getResultList();
    }

    public List<Trainer> findNotAssignedToTrainee(String traineeUsername) {
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