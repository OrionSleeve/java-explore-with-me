package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.event.enumEvent.RequestUpdateStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class RequestStatusUpdateDto {
    @Size(min = 1)
    private Set<Integer> requestIds;
    @NotNull
    private RequestUpdateStatus status;
}
