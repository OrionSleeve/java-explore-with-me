package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.Constants;
import ru.practicum.exception.DateRangeException;
import ru.practicum.mapper.HitMapper;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatsRepository;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final HitMapper endpointHitMapper;
    private final StatsMapper viewStatsMapper;
    private final StatsRepository repository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

    @Override
    public void createEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit entity = repository.save(endpointHitMapper.toEntity(endpointHitDto));
        log.info("Endpoint hit: {} was saved to database", entity);
    }

    @Override
    public List<ViewStatsDto> getStatistics(String start, String end, String[] uris, boolean unique) {
        LocalDateTime startDate = decodeDate(start);
        LocalDateTime endDate = decodeDate(end);

        if (startDate.isAfter(endDate)) {
            throw new DateRangeException("End date should be after start date");
        }
        List<ViewStats> viewViewStatsEntities;

        if (unique && uris.length == 0) {
            viewViewStatsEntities = repository.getStatsForAllEndpointHitsWithUniqueIp(startDate, endDate);
        } else if (unique) {
            viewViewStatsEntities = repository.getStatsForEndpointHitsWithUniqueIp(startDate, endDate, uris);
        } else if (uris.length == 0) {
            viewViewStatsEntities = repository.getStatsForAllEndpointHitsWithNonUniqueIp(startDate, endDate);
        } else {
            viewViewStatsEntities = repository.getStatsForEndpointHitsWithNonUniqueIp(startDate, endDate, uris);
        }

        log.info("Received statistics from database: {}", viewViewStatsEntities);

        return viewViewStatsEntities.stream().map(viewStatsMapper::toDto).collect(Collectors.toList());
    }

    private LocalDateTime decodeDate(String date) {
        String dateEncoded = UriUtils.decode(date, StandardCharsets.UTF_8);
        return LocalDateTime.parse(dateEncoded, formatter);
    }
}