package ru.practicum.compilation.mapper;

import org.mapstruct.Mapper;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.model.CompilationEntity;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.ShortEventMapper;
import ru.practicum.event.model.EventEntity;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ShortEventMapper.class})
public interface CompilationMapper {
    List<EventShortDto> toEventShortDtoList(List<EventEntity> eventEntities);

    CompilationDto toDto(CompilationEntity compilationEntity);
}
