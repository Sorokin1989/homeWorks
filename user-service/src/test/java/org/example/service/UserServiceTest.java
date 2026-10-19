package org.example.service;

import org.example.dto.UserNotificationEvent;
import org.example.dto.UserPatchDto;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.example.exception.UserExistException;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.producer.UserEventProducer;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    private UserEventProducer  producer;

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

        ArgumentCaptor<UserNotificationEvent> eventCaptor = ArgumentCaptor.forClass(UserNotificationEvent.class);
        verify(producer).send(eventCaptor.capture());

        UserNotificationEvent sentEvent = eventCaptor.getValue();
        assertEquals(UserNotificationEvent.Operation.CREATE, sentEvent.getOperation());
        assertEquals("asd@mail.ru", sentEvent.getEmail());

        verify(userRepository).save(user);
        verify(userRepository).existsByEmail(userRequestDto.getEmail());

        assertEquals(userResponseDto, dto);

    }

    @Test
    void createUserWhenEmailExists() {
        UserRequestDto userRequestDto = new UserRequestDto("dima", 25, "asd@mail.ru");

        User user = new User(1L, "dima", 25, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4));

        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(true);

        assertThrows(UserExistException.class,
                () -> userService.createUser(userRequestDto));

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository, never()).save(any(User.class));

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

        assertThrows(UserNotFoundException.class, () -> userService.findUserById(1L));
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

        List<UserResponseDto> actual = userService.findAllUsers();

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
                () -> userService.updateUser(1L, userRequestDto));


        verify(userRepository).findById(1L);
        verify(userMapper, never()).updateUserFromDto(any(), any());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).userToUserResponseDto(any(User.class));
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
        doAnswer(inv -> {
            UserRequestDto dto = inv.getArgument(0);
            User target = inv.getArgument(1);
            target.setName(dto.getName());
            target.setAge(dto.getAge());
            target.setEmail(dto.getEmail());
            return null;
        }).when(userMapper).updateUserFromDto(any(UserRequestDto.class), any(User.class));

        UserResponseDto updateDto = userService.updateUser(1L, userRequestDto);

        assertEquals(userResponseDtoUpdate, updateDto);

        assertEquals(userResponseDtoUpdate.getName(), updateDto.getName());
        assertEquals(userResponseDtoUpdate.getAge(), updateDto.getAge());
        assertEquals(userResponseDtoUpdate.getEmail(), updateDto.getEmail());

        assertEquals(updateDto.getName(),user.getName());
        assertEquals(updateDto.getAge(),user.getAge());
        assertEquals(updateDto.getEmail(),user.getEmail());


        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        verify(userMapper).updateUserFromDto(userRequestDto, user);
        verify(userMapper).userToUserResponseDto(user);

    }

    @Test
    void updateUserPatchThrowExceptionWhenUserNotFound() {

        UserPatchDto userPatchDto = new UserPatchDto("sasha", 35, "www@mail.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUserPatch(1L, userPatchDto));

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).userToUserResponseDto(any(User.class));
        verify(userMapper, never()).updateUserPatchDto(any(UserPatchDto.class), any(User.class));

    }

    @Test
    void updateUserPatchToNameSuccess() {
        UserPatchDto userPatchDto = new UserPatchDto("sasha", null, null);

        User user = new User(1L, "dima", 25, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4));

        UserResponseDto userResponseDtoUpdate = new UserResponseDto(
                1L, "sasha", 25, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponseDto(any(User.class))).thenReturn(userResponseDtoUpdate);
        doAnswer(inv -> {
            UserPatchDto dto = inv.getArgument(0);
            User target = inv.getArgument(1);
            if (dto.getName() != null) target.setName(dto.getName());
            if (dto.getAge() != null) target.setAge(dto.getAge());
            if (dto.getEmail() != null) target.setEmail(dto.getEmail());
            return null;
        }).when(userMapper).updateUserPatchDto(any(UserPatchDto.class), any(User.class));


        UserResponseDto updatedDto = userService.updateUserPatch(1L, userPatchDto);

        assertEquals(userResponseDtoUpdate.getId(), updatedDto.getId());
        assertEquals(userResponseDtoUpdate.getName(), updatedDto.getName());
        assertEquals(userResponseDtoUpdate.getAge(), updatedDto.getAge());
        assertEquals(userResponseDtoUpdate.getEmail(), updatedDto.getEmail());

        assertEquals(userPatchDto.getName(), user.getName());
        assertEquals(userResponseDtoUpdate.getAge(), user.getAge());
        assertEquals(userResponseDtoUpdate.getEmail(), user.getEmail());


        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        verify(userMapper).userToUserResponseDto(user);
        verify(userMapper).updateUserPatchDto(userPatchDto, user);
    }

    @Test
    void updateUserPatchToAgeSuccess() {
        UserPatchDto userPatchDto = new UserPatchDto(null, 35, null);

        User user = new User(1L, "dima", 25, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4));

        UserResponseDto userResponseDtoUpdate = new UserResponseDto(
                1L, "dima", 35, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponseDto(any(User.class))).thenReturn(userResponseDtoUpdate);
        doAnswer(inv -> {
            UserPatchDto dto = inv.getArgument(0);
            User target = inv.getArgument(1);
            if (dto.getName() != null) target.setName(dto.getName());
            if (dto.getAge() != null) target.setAge(dto.getAge());
            if (dto.getEmail() != null) target.setEmail(dto.getEmail());
            return null;
        }).when(userMapper).updateUserPatchDto(any(UserPatchDto.class), any(User.class));


        UserResponseDto updatedDto = userService.updateUserPatch(1L, userPatchDto);

        assertEquals(userResponseDtoUpdate.getId(), updatedDto.getId());
        assertEquals(userResponseDtoUpdate.getName(), updatedDto.getName());
        assertEquals(userResponseDtoUpdate.getAge(), updatedDto.getAge());
        assertEquals(userResponseDtoUpdate.getEmail(), updatedDto.getEmail());

        assertEquals(userResponseDtoUpdate.getName(), user.getName());
        assertEquals(userPatchDto.getAge(), user.getAge());
        assertEquals(userResponseDtoUpdate.getEmail(), user.getEmail());


        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        verify(userMapper).userToUserResponseDto(user);
        verify(userMapper).updateUserPatchDto(userPatchDto, user);
    }

    @Test
    void updateUserPatchToEmailSuccess() {
        UserPatchDto userPatchDto = new UserPatchDto(null, null, "www@mail.ru");

        User user = new User(1L, "dima", 25, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4));

        UserResponseDto userResponseDtoUpdate = new UserResponseDto(
                1L, "dima", 25, "www@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponseDto(any(User.class))).thenReturn(userResponseDtoUpdate);
        doAnswer(inv -> {
            UserPatchDto dto = inv.getArgument(0);
            User target = inv.getArgument(1);
            if (dto.getName() != null) target.setName(dto.getName());
            if (dto.getAge() != null) target.setAge(dto.getAge());
            if (dto.getEmail() != null) target.setEmail(dto.getEmail());
            return null;
        }).when(userMapper).updateUserPatchDto(any(UserPatchDto.class), any(User.class));


        UserResponseDto updatedDto = userService.updateUserPatch(1L, userPatchDto);

        assertEquals(userResponseDtoUpdate.getId(), updatedDto.getId());
        assertEquals(userResponseDtoUpdate.getName(), updatedDto.getName());
        assertEquals(userResponseDtoUpdate.getAge(), updatedDto.getAge());
        assertEquals(userResponseDtoUpdate.getEmail(), updatedDto.getEmail());

        assertEquals(userResponseDtoUpdate.getName(), user.getName());
        assertEquals(userResponseDtoUpdate.getAge(), user.getAge());
        assertEquals(userPatchDto.getEmail(), user.getEmail());


        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        verify(userMapper).userToUserResponseDto(user);
        verify(userMapper).updateUserPatchDto(userPatchDto, user);
    }

    @Test
    void deleteUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUserById(1L));

        verify(userRepository).findById(1L);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void deleteUserSuccess() {

        User user = new User(1L, "dima", 25, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUserById(1L);

        ArgumentCaptor<UserNotificationEvent> eventCaptor = ArgumentCaptor.forClass(UserNotificationEvent.class);
        verify(producer).send(eventCaptor.capture());

        UserNotificationEvent sentEvent = eventCaptor.getValue();
        assertEquals(UserNotificationEvent.Operation.DELETE, sentEvent.getOperation());
        assertEquals("asd@mail.ru", sentEvent.getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);


    }
}