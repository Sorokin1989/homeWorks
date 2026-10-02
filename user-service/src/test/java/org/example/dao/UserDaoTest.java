package org.example.dao;

import org.example.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoTest {

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
    void saveUserUserIsNull() throws Exception {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    userDao.save(null);
                });


    }

    @Test
    void saveUserSuccess() throws Exception {

        User user = new User("дима", 35, "qedfq@mail.ru");

        User savedUser = userDao.save(user);

        assertSame(user, savedUser);

        assertNotNull(savedUser.getId(), "Id должен быть установлен");
        assertNotNull(savedUser.getCreatedAt(), "CreateAt должен быть установлен");

        try (Session session = sessionFactory.openSession()) {
            User userFromDb = session.get(User.class, savedUser.getId());

            assertNotNull(userFromDb, "Пользователь должен быть в БД");
            assertEquals(user.getName(), userFromDb.getName());
            assertEquals(user.getAge(), userFromDb.getAge());
            assertEquals(user.getEmail(), userFromDb.getEmail());
        }
    }

    @Test
    void saveUserDuplicateEmailThrowsException() throws Exception {

        User userOne = new User("dima", 25, "www@mail.ru");
        userDao.save(userOne);

        User userTwo = new User("pasha", 35, "www@mail.ru");

        assertThrows(Exception.class, () -> {
            userDao.save(userTwo);
        });

    }

    @Test
    void saveUserNameIsNullThrowsException() throws Exception {

        User user = new User(null, 25, "www@mail.ru");

        assertThrows(Exception.class, () -> {
            userDao.save(user);
        });

    }

    @Test
    void saveUserAgeIsNullThrowsException() throws Exception {

        User user = new User("dima", null, "www@mail.ru");
        assertThrows(Exception.class, () -> {
            userDao.save(user);
        });
    }

    @Test
    void findUserByIdSuccess() throws Exception {

        User user = new User("dima", 35, "www@mail.ru");
        User savedUser = userDao.save(user);

        User findUser = userDao.findById(savedUser.getId());

        assertNotNull(findUser);
        assertEquals(user.getName(), findUser.getName());
        assertEquals(user.getAge(), findUser.getAge());
        assertEquals(user.getEmail(), findUser.getEmail());


    }

    @Test
    void notFindUserByIdReturnNull() throws Exception {

        assertNull(userDao.findById(-999L));
    }

    @Test
    void findUserAllSuccess() throws Exception {
        User user1 = new User("dima", 35, "www@mail.ru");
        User user2 = new User("pasha", 33, "ddd@rambler.ru");
        userDao.save(user1);
        userDao.save(user2);


        List<User> users = userDao.findAll();
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(user1.getId(), users.getFirst().getId());
        assertEquals(user1.getName(), users.getFirst().getName());
        assertEquals(user1.getAge(), users.getFirst().getAge());
        assertEquals(user1.getEmail(), users.getFirst().getEmail());
        assertEquals(user2.getId(), users.get(1).getId());
        assertEquals(user2.getName(), users.get(1).getName());
        assertEquals(user2.getAge(), users.get(1).getAge());
        assertEquals(user2.getEmail(), users.get(1).getEmail());
    }

    @Test
    void findUsersIsEmpty() throws Exception {
        List<User> users = userDao.findAll();

        assertNotNull(users);
        assertTrue(users.isEmpty());
        assertEquals(0, users.size());

    }

    @Test
    void deleteUserByIdUserNotNull() throws Exception {
        User user = new User("dima", 35, "www@mail.ru");
        userDao.save(user);


        userDao.deleteById(user.getId());
        User deletedUser = userDao.findById(user.getId());
        assertNull(deletedUser, "Пользователь должен быть удален!");

    }

    @Test
    void deleteUserByIdNotFound() throws Exception {
        assertDoesNotThrow(() -> userDao.deleteById(999L));
    }

    @Test
    void updateUserSuccess() throws Exception {
        User user = new User("dima", 35, "www@mail.ru");
        User savedUser = userDao.save(user);
        LocalDateTime originalCreatedAt = savedUser.getCreatedAt();

        savedUser.setName("pasha");
        savedUser.setAge(33);
        savedUser.setEmail("eee@mail.ru");
        userDao.update(savedUser);

        User updateUser = userDao.findById(savedUser.getId());

        assertEquals(savedUser.getName(), updateUser.getName());
        assertEquals(savedUser.getAge(), updateUser.getAge());
        assertEquals(savedUser.getEmail(), updateUser.getEmail());
        assertEquals(originalCreatedAt, updateUser.getCreatedAt());
    }

    @Test
    void updateUserNullThrowsException() throws Exception {
        assertThrows(Exception.class, () -> {
            userDao.update(null);
        });
    }

    @Test
    void updateUser_notFound_throwsException() {
        User user = new User("dima", 30, "dima@mail.ru");
        user.setId(999L);

        assertThrows(Exception.class, () -> userDao.update(user));
    }

}