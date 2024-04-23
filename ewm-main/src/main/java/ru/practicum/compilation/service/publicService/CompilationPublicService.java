package ru.practicum.compilation.service.publicService;

import ru.practicum.compilation.dto.CompilationDto;

import java.util.List;

public interface CompilationPublicService {
    CompilationDto getCompilationById(Integer compId);

    List<CompilationDto> searchCompilations(Boolean pinned, Integer from, Integer size);
}
