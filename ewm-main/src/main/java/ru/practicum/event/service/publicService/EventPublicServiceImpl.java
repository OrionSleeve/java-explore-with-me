package ru.practicum.event.service.publicService;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.EventFieldSet;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.UserSearchRequestDto;
import ru.practicum.event.enumEvent.EventSort;
import ru.practicum.event.enumEvent.EventStatus;
import ru.practicum.event.exception.DateRangeException;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.ShortEventMapper;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.model.QEventEntity;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventPublicServiceImpl implements EventPublicService {
    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final EventFieldSet eventFieldSet;
    private final EventMapper fullEventMapper;
    private final ShortEventMapper shortEventMapper;

    @Override
    public EventFullDto getEventById(Integer eventId, EndpointHitDto endpointHitDto) {
        EventEntity eventEntity = eventRepository.findByIdAndState(eventId, EventStatus.PUBLISHED);

        if (eventEntity == null) {
            throw new NotFoundException(String.format(String.format("Event with id=%d was not found", eventId)));
        }

        eventFieldSet.setConfirmedRequests(eventEntity);
        eventFieldSet.setViews(eventEntity);

        statsClient.createEndpointHit(endpointHitDto);

        EventFullDto eventFullDto = fullEventMapper.toDto(eventEntity);

        return eventFullDto;
    }

    @Override
    public List<EventShortDto> searchEvents(UserSearchRequestDto dto, EndpointHitDto endpointHitDto) {
        if (dto.getRangeStart() != null && dto.getRangeEnd() != null && dto.getRangeStart().isAfter(dto.getRangeEnd())) {
            throw new DateRangeException("RangeStart should be before RangeEnd");
        }

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(QEventEntity.eventEntity.state.eq(EventStatus.PUBLISHED));

        if (dto.getText() != null) {
            predicates.add(QEventEntity.eventEntity.annotation.containsIgnoreCase(dto.getText())
                    .or(QEventEntity.eventEntity.description.containsIgnoreCase(dto.getText())));
        }

        if (dto.getCategories() != null) {
            predicates.add(QEventEntity.eventEntity.category.id.in(dto.getCategories()));
        }

        if (dto.getPaid() != null) {
            predicates.add(QEventEntity.eventEntity.paid.eq(dto.getPaid()));
        }

        if (dto.getRangeStart() != null) {
            predicates.add(QEventEntity.eventEntity.eventDate.after(dto.getRangeStart()));
        }

        if (dto.getRangeEnd() != null) {
            predicates.add(QEventEntity.eventEntity.eventDate.before(dto.getRangeEnd()));
        }

        if (dto.getRangeStart() == null && dto.getRangeEnd() == null) {
            predicates.add(QEventEntity.eventEntity.eventDate.after(LocalDateTime.now()));
        }

        Predicate predicate = ExpressionUtils.allOf(predicates);

        Iterable<EventEntity> foundEventsAsIterable = eventRepository.findAll(predicate);
        List<EventEntity> foundEvents = new ArrayList<>();
        foundEventsAsIterable.forEach(foundEvents::add);

        eventFieldSet.setViews(foundEvents);
        eventFieldSet.setConfirmedRequests(foundEvents);

        if (dto.getOnlyAvailable()) {
            foundEvents = foundEvents.stream()
                    .filter(eventEntity -> eventEntity.getParticipantLimit() == 0
                            || eventEntity.getParticipantLimit() > eventEntity.getConfirmedRequests())
                    .collect(Collectors.toList());
        }

        if (dto.getSort() == EventSort.EVENT_DATE) {
            foundEvents = foundEvents.stream().sorted(Comparator.comparing(EventEntity::getEventDate))
                    .skip(dto.getFrom()).limit(dto.getSize()).collect(Collectors.toList());
        } else {
            foundEvents = foundEvents.stream().sorted(Comparator.comparing(EventEntity::getViews))
                    .skip(dto.getFrom()).limit(dto.getSize()).collect(Collectors.toList());
        }

        List<EventShortDto> eventShortDtos = foundEvents.stream().map(shortEventMapper::toDto)
                .collect(Collectors.toList());

        statsClient.createEndpointHit(endpointHitDto);

        return eventShortDtos;
    }
}
