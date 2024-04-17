package ru.practicum.mapper;

import ru.practicum.model.EndpointHit;
import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.EndpointHitDto;

@Mapper(componentModel = "spring")
public interface HitMapper {
    EndpointHit toEntity(EndpointHitDto endpointHitDto);
}
