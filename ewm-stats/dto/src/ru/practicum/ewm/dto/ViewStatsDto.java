package ru.practicum.ewm.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ViewStatsDto {
    String app;
    String uri;
    Integer hits;
}
