package org.example.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.dto.UserPatchDto;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users",description = "Контроллер для работы с пользователями")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @ApiResponses(value = {
          @ApiResponse(responseCode = "201",
                  description = "success",
                  content = @Content(schema = @Schema (implementation = UserResponseDto.class))
          ),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request",
                    content = @Content(schema = @Schema (implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "409",
                    description = "Email already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        return userService.createUser(userRequestDto);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Список пользователей",
            content = @Content(array = @ArraySchema( schema = @Schema (implementation = UserResponseDto.class)))),

    })
    @GetMapping
    public List<UserResponseDto> findAll() {
        return userService.findAllUsers();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Ok",
            content = @Content(schema = @Schema (implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404",
            description = "Not found",
            content = @Content(schema = @Schema (implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
            description = "Bad request",
            content = @Content(schema = @Schema (implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public UserResponseDto findUserById(@PathVariable @Positive Long id) {
        return userService.findUserById(id);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Ok",
            content = @Content(schema = @Schema (implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404",
            description = "Not found",
            content = @Content(schema = @Schema (implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
            description = "Bad request",
            content = @Content(schema = @Schema (implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409",
            description = "Conflict",
            content = @Content(schema = @Schema (implementation = ErrorResponse.class))),
    })
    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable @Positive Long id,
                                                      @Valid @RequestBody UserRequestDto userRequestDto) {
        return userService.updateUser(id, userRequestDto);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ok",
                    content = @Content(schema = @Schema (implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    description = "Not found",
                    content = @Content(schema = @Schema (implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema (implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "Conflict",
                    content = @Content(schema = @Schema (implementation = ErrorResponse.class))),
    })
    @PatchMapping("/{id}")
    public UserResponseDto updateUserPatch(@PathVariable @Positive Long id,
                                                           @Valid @RequestBody UserPatchDto userPatchDto) {
        return userService.updateUserPatch(id, userPatchDto);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
            description = "No content",
            content = @Content),
            @ApiResponse(responseCode = "404",
            description = "Not found",
            content = @Content(schema = @Schema (implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
            description = "Bad request",
            content = @Content(schema = @Schema (implementation = ErrorResponse.class))),
    })

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable @Positive Long id) {
        userService.deleteUserById(id);
    }
}
