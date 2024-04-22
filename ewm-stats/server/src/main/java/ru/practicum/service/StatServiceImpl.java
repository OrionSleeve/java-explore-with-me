package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.mapper.HitMapper;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatsRepository;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final HitMapper endpointHitMapper;
    private final StatsMapper viewStatsMapper;
    private final StatsRepository repository;

    @Override
    public void createEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit entity = repository.save(endpointHitMapper.toEntity(endpointHitDto));
        log.info("Endpoint hit: {} was saved to database", entity);
    }

    @Override
    public List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        List<ViewStats> viewViewStatsEntities;

        if (unique && uris.length == 0) {
            viewViewStatsEntities = repository.getStatsForAllEndpointHitsWithUniqueIp(start, end);
        } else if (unique) {
            viewViewStatsEntities = repository.getStatsForEndpointHitsWithUniqueIp(start, end, uris);
        } else if (uris.length == 0) {
            viewViewStatsEntities = repository.getStatsForAllEndpointHitsWithNonUniqueIp(start, end);
        } else {
            viewViewStatsEntities = repository.getStatsForEndpointHitsWithNonUniqueIp(start, end, uris);
        }

        log.info("Received statistics from database: {}", viewViewStatsEntities);

        return viewViewStatsEntities.stream().map(viewStatsMapper::toDto).collect(Collectors.toList());
    }
}