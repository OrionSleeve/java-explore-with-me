package ru.practicum.mapper;

import ru.practicum.model.ViewStats;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.ViewStatsDto;

@Component
public class StatsMapper {
    public ViewStatsDto toDto(ViewStats viewStatsEntity) {
        return new ViewStatsDto(viewStatsEntity.getApp(), viewStatsEntity.getUri(), viewStatsEntity.getHits().intValue());
    }
}
