package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;



    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto userRequestDto) {


        return userService.createUser(userRequestDto);
    }

    @GetMapping
    public List<UserResponseDto> findAll() {

       return userService.findAllUsers();

    }

    @GetMapping("/{id}")
    public UserResponseDto findUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Long id,@RequestBody UserRequestDto userRequestDto) {
        return userService.updateUser(id,userRequestDto);
    }


    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);

    }
}
