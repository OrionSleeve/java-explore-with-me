package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.categories.model.CategoryEntity;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.EventEntity;

@Mapper(componentModel = "spring", uses = {LocationMapper.class})
public interface NewEventMapper {
    EventEntity toEntity(NewEventDto eventDto);

    default CategoryEntity map(Integer value) {
        return CategoryEntity.builder().id(value).build();
    }
}
