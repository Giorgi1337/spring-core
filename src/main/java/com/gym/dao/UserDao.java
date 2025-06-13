package com.gym.dao;

import com.gym.model.User;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends AbstractDao<User, Long> {

    public UserDao(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
    }

    public boolean existsByUsername(String username) {
        return getSession()
                .createQuery("SELECT 1 FROM User u WHERE u.username = :username", Integer.class)
                .setParameter("username", username)
                .setMaxResults(1)
                .uniqueResultOptional()
                .isPresent();
    }
}