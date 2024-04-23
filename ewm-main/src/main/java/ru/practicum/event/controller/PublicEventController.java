package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.UserSearchRequestDto;
import ru.practicum.event.enumEvent.EventSort;
import ru.practicum.event.service.publicService.EventPublicService;
import ru.practicum.ewm.Constants;
import ru.practicum.ewm.dto.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {
    private final EventPublicService service;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable(name = "id") @NotNull @Min(1) Integer eventId,
                                     HttpServletRequest request) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder().app("ewm-ewm-main")
                .uri(request.getRequestURI()).ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now()).build();
        log.info("Getting event by id {}", eventId);
        EventFullDto event = service.getEventById(eventId, endpointHitDto);

        log.info("Received event: {}, endpoint hit saved: {}", event, endpointHitDto);

        return event;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> searchEvents(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) Integer[] categories,
                                            @RequestParam(required = false) Boolean paid,
                                            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                            @RequestParam(required = false)
                                            @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDateTime rangeStart,
                                            @RequestParam(required = false)
                                            @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDateTime rangeEnd,
                                            @RequestParam(defaultValue = "EVENT_DATE") EventSort sort,
                                            @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                            @RequestParam(defaultValue = "10") @Min(1) Integer size,
                                            HttpServletRequest request) {

        EndpointHitDto endpointHitDto = EndpointHitDto.builder().app("ewm-ewm-main")
                .uri(request.getRequestURI()).ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now()).build();

        UserSearchRequestDto searchRequestDto = UserSearchRequestDto.builder().text(text).categories(categories)
                .paid(paid).onlyAvailable(onlyAvailable).rangeStart(rangeStart).rangeEnd(rangeEnd).sort(sort)
                .from(from).size(size).build();

        log.info("Searching events by params: {}", searchRequestDto);
        List<EventShortDto> events = service.searchEvents(searchRequestDto, endpointHitDto);
        log.info("Received list of events {} for search request {}", events, searchRequestDto);

        return events;
    }
}
