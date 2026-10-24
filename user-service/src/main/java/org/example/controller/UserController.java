package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.example.exception.ErrorResponse;
import org.example.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Users", description = "Контроллер для работы с пользователями")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "success (HATEOAS: _links — self, users)",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "Email already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать пользователя", description = "Регистрирует нового пользователя")
    public EntityModel<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto created = userService.createUser(userRequestDto);
        return EntityModel.of(created,
                linkTo(methodOn(UserController.class).findUserById(created.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).findAll()).withRel("users"));
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Список пользователей (HATEOAS: _embedded, _links — self)",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class)))),
    })
    @GetMapping
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей")
    public CollectionModel<EntityModel<UserResponseDto>> findAll() {
        List<EntityModel<UserResponseDto>> users = userService.findAllUsers().stream()
                .map(u -> EntityModel.of(u,
                        linkTo(methodOn(UserController.class).findUserById(u.getId())).withSelfRel()))
                .toList();
        return CollectionModel.of(users,
                linkTo(methodOn(UserController.class).findAll()).withSelfRel());
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ok (HATEOAS: _links — self, users, update, delete)",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    description = "Not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    @Operation(summary = "Покажи пользователя по id", description = "Показывает пользователя по определенному id")
    public EntityModel<UserResponseDto> findUserById(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable @Positive Long id) {
        UserResponseDto user = userService.findUserById(id);
        Link deleteLink = Link.of(
                        linkTo(UserController.class).slash(id).toUri().toString())
                .withRel("delete");
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).findUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).findAll()).withRel("users"),
                linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update"),
                deleteLink);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ok (HATEOAS: _links — self, users)",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    description = "Not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "Conflict",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя", description = "Обновляет пользователя")
    public EntityModel<UserResponseDto> updateUser(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable @Positive Long id,
            @Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto updated = userService.updateUser(id, userRequestDto);
        return EntityModel.of(updated,
                linkTo(methodOn(UserController.class).findUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).findAll()).withRel("users"));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ok (HATEOAS: _links — self, users)",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    description = "Not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "Conflict",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PatchMapping("/{id}")
    @Operation(summary = "Частично обновить пользователя", description = "Частично обновить пользователя")
    public EntityModel<UserResponseDto> updateUserPatch(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable @Positive Long id,
            @Valid @RequestBody UserPatchDto userPatchDto) {
        UserResponseDto updated = userService.updateUserPatch(id, userPatchDto);
        return EntityModel.of(updated,
                linkTo(methodOn(UserController.class).findUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).findAll()).withRel("users"));
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "No content",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить пользователя по id", description = "Удаляет пользователя по id")
    public void deleteUserById(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable @Positive Long id) {
        userService.deleteUserById(id);
    }
}