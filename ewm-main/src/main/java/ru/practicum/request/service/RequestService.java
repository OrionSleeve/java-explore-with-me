package ru.practicum.request.service;

import ru.practicum.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto createRequest(Integer userId, Integer eventId);

    List<RequestDto> getUserRequests(Integer userId);

    RequestDto cancelRequest(Integer userId, Integer requestId);
}
