package ru.practicum.compilation.service.adminService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.dto.CompilationUpdateDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.CompilationEntity;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.EventFieldSet;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CompilationAdminServiceImpl implements CompilationAdminService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventFieldSet eventFieldSet;
    private final CompilationMapper compilationMapper;


    @Override
    public CompilationDto createCompilation(CompilationNewDto dto) {
        CompilationEntity compilationEntity = new CompilationEntity();
        compilationEntity.setTitle(dto.getTitle());
        compilationEntity.setPinned(Objects.requireNonNullElse(dto.getPinned(), false));

        if (dto.getEvents() == null) {
            compilationEntity.setEvents(new ArrayList<>());
        } else {
            List<EventEntity> eventEntities = eventRepository.findByIdIn(dto.getEvents());
            if (eventEntities.size() != dto.getEvents().size()) {
                Set<Integer> foundedEvents = eventEntities.stream().map(EventEntity::getId).collect(Collectors.toSet());
                dto.getEvents().removeAll(foundedEvents);
                throw new NotFoundException(String.format("Events with ids=%s were not found", dto.getEvents()));
            }
            eventFieldSet.setViews(eventEntities);
            eventFieldSet.setConfirmedRequests(eventEntities);
            compilationEntity.setEvents(eventEntities);
        }
        compilationEntity = compilationRepository.save(compilationEntity);
        CompilationDto compilationDto = compilationMapper.toDto(compilationEntity);

        return compilationDto;
    }

    @Override
    public CompilationDto updateCompilation(Integer compId, CompilationUpdateDto dto) {
        CompilationEntity compilationEntity = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id=%d was not found", compId)));

        compilationEntity.setTitle(Objects.requireNonNullElse(dto.getTitle(), compilationEntity.getTitle()));
        compilationEntity.setPinned(Objects.requireNonNullElse(dto.getPinned(), compilationEntity.getPinned()));

        if (dto.getEvents() != null && dto.getEvents().size() == 0) {
            compilationEntity.setEvents(new ArrayList<>());
        } else if (dto.getEvents() != null) {
            List<EventEntity> eventEntities = eventRepository.findByIdIn(dto.getEvents());
            if (eventEntities.size() != dto.getEvents().size()) {
                Set<Integer> foundedEvents = eventEntities.stream().map(EventEntity::getId).collect(Collectors.toSet());
                dto.getEvents().removeAll(foundedEvents);
                throw new NotFoundException(String.format("Events with ids=%s were not found", dto.getEvents()));
            }
            eventFieldSet.setViews(eventEntities);
            eventFieldSet.setConfirmedRequests(eventEntities);
            compilationEntity.setEvents(eventEntities);
        }
        compilationEntity = compilationRepository.save(compilationEntity);
        CompilationDto compilationDto = compilationMapper.toDto(compilationEntity);

        return compilationDto;
    }

    @Override
    public void removeCompilation(Integer compId) {
        compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id=%d was not found", compId)));

        compilationRepository.deleteById(compId);
    }
}
