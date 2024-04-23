package ru.practicum.service;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.util.List;

public interface StatService {
    void createEndpointHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStatistics(String start, String end, String[] uris, boolean unique);
}
