package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.publicService.CompilationPublicService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class CompilationPublicController {
    private final CompilationPublicService service;

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(@PathVariable @NotNull @Min(1) Integer compId) {
        log.info("Received request to get compilation by ID: {}", compId);
        return service.getCompilationById(compId);
    }

    @GetMapping
    public List<CompilationDto> searchCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                   @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Received request to search compilations with parameters: pinned={}, from={}, size={}", pinned, from, size);
        return service.searchCompilations(pinned, from, size);
    }
}