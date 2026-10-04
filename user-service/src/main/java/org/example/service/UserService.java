package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserResponseDto createUser(UserRequestDto userRequestDto) {

        User user = userMapper.userRequestDtoToUser(userRequestDto);

        User savedUser = userRepository.save(user);

        return userMapper.userToUserResponseDto(savedUser);


    }

    public UserResponseDto findUserById(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Некорректный id " + id);
        }
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с id " + id + " не существует"));

        return userMapper.userToUserResponseDto(findUser);


    }

    public List<UserResponseDto> findAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::userToUserResponseDto).toList();


    }

    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Некорректное значение id " + id);
        }

        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с таким id нет  " + id));

        userMapper.updateUserFromDto(userRequestDto, findUser);

        userRepository.save(findUser);

        return userMapper.userToUserResponseDto(findUser);


    }


    public void deleteUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Некорректное значение id: " + id);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с таким id нет! " + id));
        userRepository.delete(user);


    }


}











