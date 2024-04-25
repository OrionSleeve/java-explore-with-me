package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "subscribedOn", ignore = true)
    UserEntity toEntity(UserDto userDto);

    @Mapping(target = "subscribedOn", ignore = true)
    UserDto toDto(UserEntity userEntity);
}
