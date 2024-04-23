package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.event.enumEvent.EventStatus;
import ru.practicum.ewm.Constants;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class EventFullDto {
    private Integer id;
    private String title;
    private String annotation;
    private String description;
    private CategoryDto category;
    private UserShortDto initiator;
    private LocationDto location;
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private LocalDateTime createdOn;
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private LocalDateTime eventDate;
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private LocalDateTime publishedOn;
    private Boolean paid;
    private Boolean requestModeration;
    private Integer participantLimit;
    private Integer confirmedRequests;
    private Integer views;
    private EventStatus state;
}
