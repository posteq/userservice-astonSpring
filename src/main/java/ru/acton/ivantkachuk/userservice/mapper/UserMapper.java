package ru.acton.ivantkachuk.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.acton.ivantkachuk.userservice.dto.UserRequestDto;
import ru.acton.ivantkachuk.userservice.dto.UserResponseDto;
import ru.acton.ivantkachuk.userservice.entity.User;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toEntity(UserRequestDto userRequestDto);
    UserResponseDto toDto(User user);
    List<UserResponseDto> toDto(List<User> users);
}
