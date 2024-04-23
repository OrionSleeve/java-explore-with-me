package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.enumEvent.EventStatus;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.enumRequest.RequestStatus;
import ru.practicum.request.exception.RequestException;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.RequestEntity;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    public RequestDto createRequest(Integer userId, Integer eventId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d was not found", userId)));

        EventEntity event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        long count = requestRepository.countByRequesterAndEvent(userId, eventId);
        if (count != 0) {
            throw new RequestException("You can not add repeated request");
        }

        if (userId.equals(event.getInitiator().getId())) {
            throw new RequestException("The initiator of event can not add participation request");
        }

        if (event.getState() != EventStatus.PUBLISHED) {
            throw new RequestException("The participation request can not be added to unpublished event");
        }

        long confirmedRequests = requestRepository.countByEventAndStatus(eventId, RequestStatus.CONFIRMED);
        if (confirmedRequests >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new RequestException(String.format("Participation request limit has been" +
                    " reached for event with id %d", eventId));
        }

        RequestStatus status = (event.getRequestModeration() && event.getParticipantLimit() != 0) ?
                RequestStatus.PENDING : RequestStatus.CONFIRMED;

        RequestEntity requestEntity = RequestEntity.builder().event(eventId).requester(userId).status(status)
                .created(LocalDateTime.now()).build();

        requestEntity = requestRepository.save(requestEntity);

        return requestMapper.toDto(requestEntity);
    }

    @Override
    public List<RequestDto> getUserRequests(Integer userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d was not found", userId)));

        List<RequestEntity> requestEntities = requestRepository.findByRequester(userId);
        return requestEntities.stream().map(requestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public RequestDto cancelRequest(Integer userId, Integer requestId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d was not found", userId)));

        RequestEntity requestEntity = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Participation request with id %d was not found", requestId)));

        if (!requestEntity.getRequester().equals(userId)) {
            throw new RequestException(String.format("User with id %d has not created participation " +
                    "request with id %d and can't cancel it", userId, requestId));
        }

        requestEntity.setStatus(RequestStatus.CANCELED);
        requestEntity = requestRepository.save(requestEntity);

        return requestMapper.toDto(requestEntity);
    }
}
