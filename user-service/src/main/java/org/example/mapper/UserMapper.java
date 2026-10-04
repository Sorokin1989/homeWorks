package org.example.mapper;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "")
    User userRequestDtoToUser(UserRequestDto userRequestDto);

    @Mapping(target = "")
    UserRequestDto userToUserRequestDto(User user);

    @Mapping(target = "")
    User userResponseDtoToUser(UserResponseDto userResponseDto);

    @Mapping(target = "")
    UserResponseDto userToUserResponseDto(User user);


}
