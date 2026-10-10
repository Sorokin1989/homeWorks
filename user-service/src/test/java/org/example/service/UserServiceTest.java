package org.example.service;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.example.exception.UserExistException;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser() {

        UserRequestDto userRequestDto = new UserRequestDto("dima", 25, "asd@mail.ru");

        UserResponseDto userResponseDto = new UserResponseDto(
                1L, "dima", 25, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4));

        User user = new User(1L, "dima", 25, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4));


        when(userMapper.userRequestDtoToUser(userRequestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.userToUserResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto dto = userService.createUser(userRequestDto);

        verify(userRepository).save(user);

        assertEquals(userResponseDto, dto);

    }

    @Test
    void createUserWhenSaveThrowException() {
        UserRequestDto userRequestDto = new UserRequestDto("dima", 25, "asd@mail.ru");

        User user = new User(1L, "dima", 25, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4));

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(UserExistException.class,
                () -> userService.createUser(userRequestDto));

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository,never()).save(any(User.class));

    }

    @Test
    void findUserByIdSuccess() {

        UserResponseDto userResponseDto = new UserResponseDto(
                1L, "dima", 25, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4));

        User user = new User(1L, "dima", 25, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponseDto(user)).thenReturn(userResponseDto);


        UserResponseDto dto = userService.findUserById(1L);

        verify(userRepository).findById(1L);
        assertEquals(userResponseDto, dto);

    }

    @Test
    void findUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,() -> userService.findUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void findAllUsers() {

        List<UserResponseDto> dtos = List.of(new UserResponseDto(
                1L, "dima", 25, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4)),
        new UserResponseDto(
                2L, "sasha", 35, "sasa@mail.ru",
                LocalDateTime.of(2026, 9, 15, 11, 5)));



        User user1 = new User(1L, "dima", 25, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4));
        User user2 = new User(2L, "sasha", 35, "sasa@mail.ru",
                LocalDateTime.of(2026, 9, 15, 11, 5));

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.userToUserResponseDto(user1)).thenReturn(dtos.get(0));
        when(userMapper.userToUserResponseDto(user2)).thenReturn(dtos.get(1));

        List<UserResponseDto> actual=userService.findAllUsers();

        verify(userRepository).findAll();
        assertEquals(2, actual.size());
        assertEquals(dtos.get(0), actual.get(0));
        assertEquals(dtos.get(1), actual.get(1));

    }

    @Test
    void updateUserThrowExceptionWhenUserNotFound() {

        UserRequestDto userRequestDto = new UserRequestDto("dima", 25, "asd@mail.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                ()->userService.updateUser(1L,userRequestDto));


        verify(userRepository).findById(1L);
        verify(userMapper, never()).updateUserFromDto(any(), any());
        verify(userRepository,never()).save(any(User.class));
        verify(userMapper,never()).userToUserResponseDto(any(User.class));
    }

    @Test
    void updateUserSuccess() {

        UserRequestDto userRequestDto = new UserRequestDto("sasha", 35, "www@mail.ru");

        UserResponseDto userResponseDtoUpdate = new UserResponseDto(
                1L, "sasha", 35, "www@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4));

        User user = new User(1L, "dima", 25, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4));

       when(userMapper.userToUserResponseDto(user)).thenReturn(userResponseDtoUpdate);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDto updateDto = userService.updateUser(1L,userRequestDto);

        assertEquals(userResponseDtoUpdate, updateDto);
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        verify(userMapper).updateUserFromDto(userRequestDto, user);
        verify(userMapper).userToUserResponseDto(user);

    }
//
//    @Test
//    void updateUserPatch() {
//    }
//
    @Test
    void deleteUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUserById(1L));

        verify(userRepository).findById(1L);
        verify(userRepository,never()).delete(any(User.class));
    }

    @Test
    void deleteUserSuccess() {

        User user = new User(1L, "dima", 25, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUserById(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);



    }
}