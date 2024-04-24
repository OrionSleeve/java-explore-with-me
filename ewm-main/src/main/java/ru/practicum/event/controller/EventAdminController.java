package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.AdminSearchRequestDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequestDto;
import ru.practicum.event.enumEvent.EventStatus;
import ru.practicum.event.service.adminService.EventAdminService;
import ru.practicum.ewm.Constants;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("admin/events")
public class EventAdminController {
    private final EventAdminService service;

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable @NotNull @Min(1) Integer eventId,
                                    @RequestBody @Valid UpdateEventAdminRequestDto dto) {
        log.info("Received request to update event with ID: {}, new data: {}", eventId, dto);
        return service.updateEvent(eventId, dto);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> searchEvents(@RequestParam(required = false) Integer[] users,
                                           @RequestParam(required = false) EventStatus[] states,
                                           @RequestParam(required = false) Integer[] categories,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDateTime rangeStart,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDateTime rangeEnd,
                                           @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Received request to search events with parameters: users={}, states={}, categories={}," +
                        " rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        AdminSearchRequestDto dto = AdminSearchRequestDto.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        return service.searchEvents(dto);
    }
}
