package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.event.enumEvent.AdminStateAction;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class UpdateEventAdminRequestDto extends BaseUpdateEventRequestDto {
    private AdminStateAction stateAction;
}
