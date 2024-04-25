package ru.practicum.event.service.adminService;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import ru.practicum.event.model.QEventEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.model.CategoryEntity;
import ru.practicum.categories.repository.CategoryRepo;
import ru.practicum.event.EventFieldSet;
import ru.practicum.event.dto.AdminSearchRequestDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequestDto;
import ru.practicum.event.enumEvent.EventStatus;
import ru.practicum.event.exception.EventException;
import ru.practicum.event.exception.EventStartTimeException;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.model.LocationEntity;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.util.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepo categoryRepository;
    private final EventFieldSet eventFieldSet;
    private final EventMapper eventMapper;

    @Override
    public EventFullDto updateEvent(Integer eventId, UpdateEventAdminRequestDto dto) {
        EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(()
                -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        EventStatus eventStatus = eventEntity.getState();

        eventEntity.setTitle(Objects.requireNonNullElse(dto.getTitle(), eventEntity.getTitle()));
        eventEntity.setDescription(Objects.requireNonNullElse(dto.getDescription(), eventEntity.getDescription()));
        eventEntity.setAnnotation(Objects.requireNonNullElse(dto.getAnnotation(), eventEntity.getAnnotation()));
        eventEntity.setPaid(Objects.requireNonNullElse(dto.getPaid(), eventEntity.getPaid()));
        eventEntity.setParticipantLimit(Objects.requireNonNullElse(dto.getParticipantLimit(),
                eventEntity.getParticipantLimit()));
        eventEntity.setRequestModeration(Objects.requireNonNullElse(dto.getRequestModeration(),
                eventEntity.getRequestModeration()));


        if (dto.getStateAction() != null && eventStatus != EventStatus.PENDING) {
            throw new EventException("Event status must be PENDING");
        }

        if (dto.getEventDate() != null && eventEntity.getState() == EventStatus.PUBLISHED
                && !eventEntity.getPublishedOn().plusHours(1).isBefore(dto.getEventDate())) {
            throw new EventStartTimeException("Event should start at time, which is not earlier than " +
                    "one hour after publication date");
        }

        if (dto.getEventDate() != null && !dto.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new EventStartTimeException("Event should start at time, which is not earlier than " +
                    "two hours after current moment");
        }

        eventEntity.setEventDate(Objects.requireNonNullElse(dto.getEventDate(), eventEntity.getEventDate()));

        if (dto.getCategory() != null) {
            CategoryEntity category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found",
                            dto.getCategory())));
            eventEntity.setCategory(category);
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case PUBLISH_EVENT:
                    if (!eventEntity.getEventDate().isAfter(LocalDateTime.now().plusHours(1))) {
                        throw new EventStartTimeException("Event should start at time, which is not earlier than " +
                                "one hour after publication date");
                    }
                    eventEntity.setState(EventStatus.PUBLISHED);
                    eventEntity.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    eventEntity.setState(EventStatus.CANCELED);
            }
        }

        if (dto.getLocation() != null) {
            LocationEntity location = eventEntity.getLocation();
            location.setLon(dto.getLocation().getLon());
            location.setLat(dto.getLocation().getLat());
            locationRepository.save(location);
            eventEntity.setLocation(location);
        }

        eventEntity = eventRepository.save(eventEntity);
        eventFieldSet.setViews(eventEntity);
        eventFieldSet.setConfirmedRequests(eventEntity);

        EventFullDto eventFullDto = eventMapper.toDto(eventEntity);

        return eventFullDto;
    }

    @Override
    public List<EventFullDto> searchEvents(AdminSearchRequestDto dto) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(Expressions.asBoolean(true).isTrue());

        if (dto.getUsers() != null) {
            predicates.add(QEventEntity.eventEntity.initiator.id.in(dto.getUsers()));
        }

        if (dto.getCategories() != null) {
            predicates.add(QEventEntity.eventEntity.category.id.in(dto.getCategories()));
        }

        if (dto.getStates() != null) {
            predicates.add(QEventEntity.eventEntity.state.in(dto.getStates()));
        }

        if (dto.getRangeStart() != null) {
            predicates.add(QEventEntity.eventEntity.eventDate.after(dto.getRangeStart()));
        }

        if (dto.getRangeEnd() != null) {
            predicates.add(QEventEntity.eventEntity.eventDate.before(dto.getRangeEnd()));
        }

        Predicate predicate = ExpressionUtils.allOf(predicates);

        Pageable pageable = Page.getPageForEvents(dto.getFrom(), dto.getSize());
        List<EventEntity> eventEntities = eventRepository.findAll(predicate, pageable).toList();

        eventFieldSet.setConfirmedRequests(eventEntities);
        eventFieldSet.setViews(eventEntities);

        return eventEntities.stream().map(eventMapper::toDto).collect(Collectors.toList());
    }
}

