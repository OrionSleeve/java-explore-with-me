package ru.practicum.event.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class LocationDto {
    @NotNull
    private Float lat;
    @NotNull
    private Float lon;
}
