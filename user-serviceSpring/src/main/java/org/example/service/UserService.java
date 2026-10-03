package org.example.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public UserResponseDto createUser(UserRequestDto userRequestDto) {

        return userRepository.save(user);
    }

    public UserRequestDto findUserById(Long id) {

        return null;

    }

    public List<UserResponseDto> findAllUsers() {

        return null;
    }

    public UserRequestDto updateUser(Long id, UserRequestDto userRequestDto) {

        return null;
    }


    public void deleteUser() {

    }


}











