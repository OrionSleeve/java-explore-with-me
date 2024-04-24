package ru.practicum.event.service.publicService;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.UserSearchRequestDto;
import ru.practicum.ewm.dto.EndpointHitDto;

import java.util.List;

public interface EventPublicService {
    EventFullDto getEventById(Integer eventId, EndpointHitDto endpointHitDto);

    List<EventShortDto> searchEvents(UserSearchRequestDto dto, EndpointHitDto endpointHitDto);
}
