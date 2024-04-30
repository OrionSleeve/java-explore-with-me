package ru.practicum.event.service.privateService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.model.CategoryEntity;
import ru.practicum.categories.repository.CategoryRepo;
import ru.practicum.event.EventFieldSet;
import ru.practicum.event.dto.*;
import ru.practicum.event.enumEvent.EventStatus;
import ru.practicum.event.enumEvent.RequestUpdateStatus;
import ru.practicum.event.exception.EventException;
import ru.practicum.event.exception.EventStartTimeException;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.NewEventMapper;
import ru.practicum.event.mapper.ShortEventMapper;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.model.LocationEntity;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.enumRequest.RequestStatus;
import ru.practicum.request.exception.RequestException;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.RequestEntity;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventPrivateServiceImpl implements EventPrivateService {
    private final RequestMapper requestMapper;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final CategoryRepo categoryRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final NewEventMapper newEventMapper;
    private final EventMapper eventMapper;
    private final ShortEventMapper shortEventMapper;
    private final EventFieldSet eventFieldSet;


    @Override
    public EventFullDto createEvent(Integer userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventStartTimeException("Event should start at time, which is not earlier than " +
                    "two hours after current moment");
        }
        UserEntity initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        EventEntity eventEntity = newEventMapper.toEntity(newEventDto);
        eventEntity.setInitiator(initiator);

        Integer categoryId = eventEntity.getCategory().getId();
        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", categoryId)));
        eventEntity.setCategory(category);

        LocationEntity location = locationRepository.save(eventEntity.getLocation());
        eventEntity.setLocation(location);

        eventEntity.setCreatedOn(LocalDateTime.now());
        eventEntity.setState(EventStatus.PENDING);

        eventEntity.setPaid(Objects.requireNonNullElse(eventEntity.getPaid(), false));
        eventEntity.setParticipantLimit(Objects.requireNonNullElse(eventEntity.getParticipantLimit(), 0));
        eventEntity.setRequestModeration(Objects.requireNonNullElse(eventEntity.getRequestModeration(), true));

        eventEntity = eventRepository.save(eventEntity);

        EventFullDto eventFullDto = eventMapper.toDto(eventEntity);
        eventFullDto.setViews(0);
        eventFullDto.setConfirmedRequests(0);

        return eventFullDto;
    }

    @Override
    public EventFullDto getEventById(Integer userId, Integer eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(()
                -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (!eventEntity.getInitiator().getId().equals(userId)) {
            throw new EventException(String.format("User with id=%d is not initiator of event " +
                    "with id=%d and can't get full information about this event", userId, eventId));
        }
        eventFieldSet.setConfirmedRequests(eventEntity);
        eventFieldSet.setViews(eventEntity);

        return eventMapper.toDto(eventEntity);
    }

    @Override
    public List<EventShortDto> getUserEvents(Integer userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        Pageable pageable = Page.getPageForEvents(from, size);


        List<EventEntity> eventEntities = eventRepository.findByInitiator_Id(userId, pageable);

        eventFieldSet.setConfirmedRequests(eventEntities);
        eventFieldSet.setViews(eventEntities);

        return eventEntities.stream().map(shortEventMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getRequestsForUserEvent(Integer userId, Integer eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        EventEntity event = eventRepository.findById(eventId).orElseThrow(()
                -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new EventException(String.format("User with id=%d can not get information " +
                    "about participation requests for event with id=%d. This user is not initiator of this " +
                    "event", userId, eventId));
        }

        List<RequestEntity> requestEntities = requestRepository.findByEvent(eventId);

        return requestEntities.stream().map(requestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEvent(Integer userId, Integer eventId, UpdateEventUserRequestDto dto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(()
                -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (!eventEntity.getInitiator().getId().equals(userId)) {
            throw new EventException(String.format("User with id=%d can not update" +
                    "event with id=%d. This user is not initiator of this event", userId, eventId));
        }

        EventStatus eventStatus = eventEntity.getState();

        if (eventStatus == EventStatus.PUBLISHED) {
            throw new EventException(String.format("Event with id=%d is in status %s and can't " +
                    "be updated", eventId, eventStatus));
        }

        if (dto.getEventDate() != null && dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventStartTimeException("Event should start at time, which is not earlier than " +
                    "two hours after current moment");
        }

        eventEntity.setTitle(Objects.requireNonNullElse(dto.getTitle(), eventEntity.getTitle()));
        eventEntity.setDescription(Objects.requireNonNullElse(dto.getDescription(), eventEntity.getDescription()));
        eventEntity.setAnnotation(Objects.requireNonNullElse(dto.getAnnotation(), eventEntity.getAnnotation()));
        eventEntity.setPaid(Objects.requireNonNullElse(dto.getPaid(), eventEntity.getPaid()));
        eventEntity.setParticipantLimit(Objects.requireNonNullElse(dto.getParticipantLimit(),
                eventEntity.getParticipantLimit()));
        eventEntity.setRequestModeration(Objects.requireNonNullElse(dto.getRequestModeration(),
                eventEntity.getRequestModeration()));
        eventEntity.setEventDate(Objects.requireNonNullElse(dto.getEventDate(), eventEntity.getEventDate()));

        if (dto.getCategory() != null) {
            CategoryEntity category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found",
                            dto.getCategory())));
            eventEntity.setCategory(category);
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case CANCEL_REVIEW:
                    eventEntity.setState(EventStatus.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    eventEntity.setState(EventStatus.PENDING);
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

        return eventMapper.toDto(eventEntity);
    }

    @Override
    public RequestStatusUpdateResultDto updateRequestStatus(Integer userId, Integer eventId, RequestStatusUpdateDto dto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(()
                -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (!eventEntity.getInitiator().getId().equals(userId)) {
            throw new EventException(String.format("User with id=%d can not update" +
                    "participation requests for event with id=%d. " +
                    "This user is not initiator of this event", userId, eventId));
        }

        List<RequestEntity> requests = requestRepository.findAllById(dto.getRequestIds());

        long confirmedRequests = requestRepository.countByEventAndStatus(eventId, RequestStatus.CONFIRMED);

        if (requests.size() != dto.getRequestIds().size()) {
            Set<Integer> foundedRequestsIds = requests.stream()
                    .map(RequestEntity::getId)
                    .collect(Collectors.toSet());
            dto.getRequestIds().removeAll(foundedRequestsIds);
            throw new NotFoundException(String.format("Participation requests with ids=%s was not found",
                    dto.getRequestIds()));
        }

        if ((eventEntity.getParticipantLimit() == 0 || !eventEntity.getRequestModeration())
                && dto.getStatus() == RequestUpdateStatus.CONFIRMED) {
            return new RequestStatusUpdateResultDto();
        }

        if (dto.getStatus() == RequestUpdateStatus.CONFIRMED) {
            long confirmedRequestsAfterRequestExecution = confirmedRequests + requests.size();
            if (confirmedRequestsAfterRequestExecution > eventEntity.getParticipantLimit()) {
                throw new RequestException(String.format("For event with id=%d participant limit " +
                        "has been reached", eventId));
            }
        }

        for (RequestEntity requestEntity : requests) {
            if (requestEntity.getStatus() != RequestStatus.PENDING) {
                throw new RequestException(String.format("Can not change status for request " +
                        "with id=%d. This request is not in PENDING status", requestEntity.getId()));
            }
        }

        RequestStatus newStatus = (dto.getStatus() == RequestUpdateStatus.CONFIRMED) ? RequestStatus.CONFIRMED :
                RequestStatus.REJECTED;

        requests.forEach(requestEntity -> requestEntity.setStatus(newStatus));

        List<RequestEntity> updatedRequests = requestRepository.saveAll(requests);

        List<RequestEntity> rejectedRequests = new ArrayList<>();

        if (dto.getStatus() == RequestUpdateStatus.CONFIRMED
                && confirmedRequests + requests.size() == eventEntity.getParticipantLimit()) {
            List<RequestEntity> pendingRequests = requestRepository.findByEventAndStatus(eventId, RequestStatus.PENDING);
            pendingRequests.forEach(requestEntity -> requestEntity.setStatus(RequestStatus.REJECTED));
            rejectedRequests = requestRepository.saveAll(pendingRequests);
        }
        List<RequestDto> updatedRequestsDto = updatedRequests.stream().map(requestMapper::toDto)
                .collect(Collectors.toList());

        List<RequestDto> rejectedRequestDto = rejectedRequests.stream().map(requestMapper::toDto)
                .collect(Collectors.toList());

        if (dto.getStatus() == RequestUpdateStatus.CONFIRMED) {
            return RequestStatusUpdateResultDto.builder()
                    .confirmedRequests(updatedRequestsDto)
                    .rejectedRequests(rejectedRequestDto).build();
        } else {
            return RequestStatusUpdateResultDto.builder()
                    .rejectedRequests(updatedRequestsDto).build();
        }
    }

    @Override
    public List<EventShortDto> getEventsBySubscription(Integer userId, Integer from, Integer size) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        Pageable pageable = Page.getPageForEvents(from, size);

        List<EventEntity> eventEntities = eventRepository.findByInitiator_IdInAndStateAndEventDateAfter(
                user.getSubscribedOn(), EventStatus.PUBLISHED, LocalDateTime.now(), pageable);

        eventFieldSet.setConfirmedRequests(eventEntities);
        eventFieldSet.setViews(eventEntities);

        return eventEntities.stream().map(shortEventMapper::toDto).collect(Collectors.toList());
    }
}
