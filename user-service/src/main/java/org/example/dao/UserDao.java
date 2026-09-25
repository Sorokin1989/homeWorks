package org.example.dao;

import org.example.entity.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserDao {

    public void save(User user) {
        try (Session session = HibernateUtil.get().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(user);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public User findById(Long id) {
        try (Session session = HibernateUtil.get().openSession()) {
            return session.get(User.class, id);
        }
    }

    public List<User> findAll() {
        try (Session session = HibernateUtil.get().openSession()) {
            return session.createQuery("from User", User.class).list();
        }
    }

    public void deleteById(Long id) {
        try (Session session = HibernateUtil.get().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User user = session.get(User.class, id);
                if (user != null) {
                    session.remove(user);
                }
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public User update(User user) {

        User mergedUser;
        try (Session session = HibernateUtil.get().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                mergedUser = session.merge(user);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
        return mergedUser;
    }

}
