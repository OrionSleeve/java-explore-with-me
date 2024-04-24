package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.categories.mapper.CategoryMapper;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.EventEntity;
import ru.practicum.user.mapper.UserShortMapper;

@Mapper(componentModel = "spring", uses = {UserShortMapper.class, LocationMapper.class, CategoryMapper.class})
public interface EventMapper {
    EventFullDto toDto(EventEntity eventEntity);
}
