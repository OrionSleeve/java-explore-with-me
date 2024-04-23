package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.RequestEntity;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    RequestDto toDto(RequestEntity requestEntity);
}
