package org.example.service;

import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserEventProducer producer;


    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {


        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new UserExistException("Пользователь с таким email уже существует");
        }
        User user = userMapper.userRequestDtoToUser(userRequestDto);
        User savedUser = userRepository.save(user);

        producer.send(new UserNotificationEvent(savedUser.getEmail(),
                UserNotificationEvent.Operation.CREATE));


        return userMapper.userToUserResponseDto(savedUser);
    }

    public UserResponseDto findUserById(Long id) {

        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с id " + id + " не существует"));

        return userMapper.userToUserResponseDto(findUser);


    }

    public List<UserResponseDto> findAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::userToUserResponseDto).toList();


    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {

        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с таким id нет  " + id));

        userMapper.updateUserFromDto(userRequestDto, findUser);

        userRepository.save(findUser);

        return userMapper.userToUserResponseDto(findUser);
    }

    @Transactional
    public UserResponseDto updateUserPatch(Long id, UserPatchDto userPatchDto) {

        User findUser = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("Пользователя с таким id нет  " + id));

        userMapper.updateUserPatchDto(userPatchDto, findUser);

        userRepository.save(findUser);
        return userMapper.userToUserResponseDto(findUser);
    }



    @Transactional
    public void deleteUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с таким id нет! " + id));

        producer.send(new UserNotificationEvent(user.getEmail(),
                UserNotificationEvent.Operation.DELETE));

        userRepository.delete(user);


    }


}











