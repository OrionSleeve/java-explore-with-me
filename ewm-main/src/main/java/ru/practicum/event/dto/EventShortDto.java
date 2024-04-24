package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.ewm.Constants;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class EventShortDto {
    private Integer id;
    private String title;
    private String annotation;
    private String description;
    private CategoryDto category;
    private UserShortDto initiator;
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private LocalDateTime eventDate;
    private Boolean paid;
    private Integer confirmedRequests;
    private Integer views;
}
