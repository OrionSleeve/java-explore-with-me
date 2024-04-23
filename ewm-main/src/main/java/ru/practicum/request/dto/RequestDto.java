package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.Constants;
import ru.practicum.request.enumRequest.RequestStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class RequestDto {
    private Integer id;
    private Integer requester;
    private Integer event;
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private LocalDateTime created;
    private RequestStatus status;
}
