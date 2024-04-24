package ru.practicum.compilation.service.adminService;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.dto.CompilationUpdateDto;

public interface CompilationAdminService {
    CompilationDto createCompilation(CompilationNewDto dto);

    CompilationDto updateCompilation(Integer compId, CompilationUpdateDto dto);

    void removeCompilation(Integer compId);
}
