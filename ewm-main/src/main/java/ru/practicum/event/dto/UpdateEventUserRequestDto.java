package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.event.enumEvent.UserStateAction;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class UpdateEventUserRequestDto extends BaseUpdateEventRequestDto {
    private UserStateAction stateAction;
}
