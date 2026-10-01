package org.example.console;

import org.example.dao.UserDao;
import org.example.dto.UserDto;
import org.example.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void notValidate() throws Exception {
        User user = new User();
        user.setName("Test");
        user.setAge(125);
        user.setEmail("www@rambler.ru");

        when(scanner.nextLine()).thenReturn(user.getName())
                .thenReturn(String.valueOf(user.getAge())).
                thenReturn(user.getEmail());

        consoleMenu.createUser();

        verify(userDao,never()).save(any(User.class));

    }

    @Test
    void createUser() throws Exception {
        User user = new User();
        user.setName("Test");
        user.setAge(25);
        user.setEmail("www@rambler.ru");

        when(scanner.nextLine()).thenReturn(user.getName())
                .thenReturn(String.valueOf(user.getAge())).thenReturn(user.getEmail());

        consoleMenu.createUser();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userDao).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getAge(), savedUser.getAge());
        assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    void createUserWhenSaveThrowsException() throws Exception {
        User user = new User();
        user.setName("test");
        user.setAge(25);
        user.setEmail("aaa@rambler.ru");

        when(scanner.nextLine())
                .thenReturn(user.getName())
                .thenReturn(String.valueOf(user.getAge()))
                .thenReturn(user.getEmail());

        doThrow(new Exception("Ошибка БД"))
                .when(userDao).save(any(User.class));

        assertDoesNotThrow(() -> consoleMenu.createUser());

        verify(userDao).save(any(User.class));



    }

    @Test
    void findUser_cancelledInput_doesNotCallDao() throws Exception {
        when(scanner.nextLine()).thenReturn("");

        consoleMenu.findUser();

        verify(userDao, never()).findById(anyLong());
    }

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
    void findListUsers() {

        List<User> users = List.of(
                new User("test", 25, "www@rambler.ru"),
                new User("test2", 35, "eee@mail.ru"));

        when(userDao.findAll()).thenReturn(users);

        consoleMenu.findAllUsers();

        verify(userDao).findAll();

        verifyNoMoreInteractions(userDao);

    }

    @Test
    void findAllUsers_callsDao() {

        when(userDao.findAll()).thenReturn(List.of());
        consoleMenu.findAllUsers();
        verify(userDao).findAll();

        verifyNoMoreInteractions(userDao);
    }

    @Test
    void updateUserIdIsNullDoesNotCallDao(){
        when(scanner.nextLine()).thenReturn("");

        consoleMenu.updateUser();

        verify(userDao, never()).findById(anyLong());
        verify(userDao,never()).update(any(User.class));
    }

    @Test
    void  updateUserUserNotFoundDoesNotCallUpdate(){
        when(scanner.nextLine()).thenReturn("1");
        when(userDao.findById(anyLong())).thenReturn(null);

        consoleMenu.updateUser();

        verify(userDao).findById(anyLong());

    }


}