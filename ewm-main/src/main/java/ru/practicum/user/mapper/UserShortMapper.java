package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.UserEntity;

@Mapper(componentModel = "spring")
public interface UserShortMapper {
    UserShortDto toDto(UserEntity userEntity);
}
