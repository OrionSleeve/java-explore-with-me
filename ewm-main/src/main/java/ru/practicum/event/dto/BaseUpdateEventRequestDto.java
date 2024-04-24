package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.Constants;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class BaseUpdateEventRequestDto {
    @Size(min = 3, max = 120)
    private String title;
    @Size(min = 20, max = 2000)
    private String annotation;
    @Size(min = 20, max = 7000)
    private String description;
    private Integer category;
    private LocationDto location;
    private Boolean paid;
    private Boolean requestModeration;
    @PositiveOrZero
    private Integer participantLimit;
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private LocalDateTime eventDate;
}
