package com.gym.dao;

import com.gym.model.*;
import jakarta.persistence.criteria.*;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TrainingDao extends AbstractDao<Training, Long> {

    public TrainingDao(SessionFactory sessionFactory) {
        super(sessionFactory, Training.class);
    }

    public List<Training> findByTraineeUsernameAndCriteria(String traineeUsername, LocalDateTime fromDate,
                                                           LocalDateTime toDate, String trainerName, String trainingTypeName) {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> root = query.from(Training.class);

        Join<Training, Trainee> traineeJoin = root.join("trainee");
        Join<Trainee, User> traineeUserJoin = traineeJoin.join("user");

        Join<Training, Trainer> trainerJoin = root.join("trainer");
        Join<Trainer, User> trainerUserJoin = trainerJoin.join("user");

        Join<Training, TrainingType> trainingTypeJoin = root.join("trainingType");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(traineeUserJoin.get("username"), traineeUsername));

        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("trainingDate"), toDate));
        }

        if (trainerName != null && !trainerName.isBlank()) {
            Expression<String> fullName = cb.concat(cb.lower(trainerUserJoin.get("firstName")), " ");
            fullName = cb.concat(fullName, cb.lower(trainerUserJoin.get("lastName")));
            predicates.add(cb.like(fullName, "%" + trainerName.toLowerCase() + "%"));
        }

        if (trainingTypeName != null && !trainingTypeName.isBlank()) {
            predicates.add(cb.equal(cb.lower(trainingTypeJoin.get("trainingTypeName")), trainingTypeName.toLowerCase()));
        }

        query.select(root).where(predicates.toArray(new Predicate[0])).distinct(true);
        return getSession().createQuery(query).getResultList();
    }
}