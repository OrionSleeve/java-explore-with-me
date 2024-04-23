package ru.practicum.event.service.adminService;

import ru.practicum.event.dto.AdminSearchRequestDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequestDto;

import java.util.List;

public interface EventAdminService {
    EventFullDto updateEvent(Integer eventId, UpdateEventAdminRequestDto dto);

    List<EventFullDto> searchEvents(AdminSearchRequestDto dto);
}
