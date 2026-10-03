package org.example.controller;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;



    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto userRequestDto) {
        return userService.createUser(userRequestDto);
    }

    @GetMapping
    public List<UserResponseDto> findAll() {
        return null;
    }

    @GetMapping("/{id}")
    public UserResponseDto findUserById(@PathVariable Long id) {
        return null;
    }

    @PutMapping
    public UserResponseDto updateUser(@RequestBody UserRequestDto userRequestDto) {
        return null;
    }
    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {

    }
}
