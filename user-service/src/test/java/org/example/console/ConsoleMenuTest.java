package org.example.console;

import org.example.dao.UserDao;
import org.example.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsoleMenuTest {

    @Mock
    UserDao userDao;

    @Mock
    Scanner scanner;

    @InjectMocks
    ConsoleMenu consoleMenu;


//    @BeforeEach
//    void setUp() {
//        consoleMenu = new ConsoleMenu(userDao, scanner);
//    }


    @Test
    void findUserById() {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setAge(25);
        user.setEmail("aaa@rambler.ru");

        when(scanner.nextLine()).thenReturn("1");
        when(userDao.findById(1L)).thenReturn(user);

        consoleMenu.findUser();

        verify(userDao).findById(1L);
    }

    @Test
    void notFindUserById() {

        when(scanner.nextLine()).thenReturn("1");
        when(userDao.findById(1L)).thenReturn(null);

        consoleMenu.findUser();

        verify(userDao).findById(1L);
    }

    @Test
    void findListUsers(){

        List<User> users = List.of(
                new User("test", 25, "www@rambler.ru"),
                new User("test2",35,"eee@mail.ru"));

        when(userDao.findAll()).thenReturn(users);

        consoleMenu.findAllUsers();

        verify(userDao).findAll();

    }

    @Test
    void notFindListUsers(){
        when(userDao.findAll()).thenReturn(null);
        consoleMenu.findAllUsers();
        verify(userDao).findAll();
    }


}