package org.example.dao;

import org.example.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class UserDaoTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    static SessionFactory sessionFactory;
    static UserDao userDao;

    @BeforeAll
    static void setup() {

//        postgres = new PostgreSQLContainer<>("postgres:16-alpine");
//        postgres.start();

        Configuration config = new Configuration();
        config.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        config.setProperty("hibernate.connection.username", postgres.getUsername());
        config.setProperty("hibernate.connection.password", postgres.getPassword());
        config.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        config.addAnnotatedClass(User.class);

        sessionFactory = config.buildSessionFactory();

      userDao = new UserDao(sessionFactory);
    }

    @AfterAll
    static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void cleanDatabase() {
        // Очистить таблицу перед каждым тестом
        try (var session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.createQuery("delete from User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    public void saveUserSuccess() throws Exception {

        User user = new User("дима",35,"qedfq@mail.ru");

       User savedUser=userDao.save(user);

       assertEquals(user,savedUser);


    }

}