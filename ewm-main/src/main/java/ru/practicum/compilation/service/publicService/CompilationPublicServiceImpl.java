package ru.practicum.compilation.service.publicService;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.CompilationEntity;
import ru.practicum.compilation.model.QCompilationEntity;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.EventFieldSet;
import ru.practicum.event.model.EventEntity;
import ru.practicum.exception.NotFoundException;
import ru.practicum.util.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CompilationPublicServiceImpl implements CompilationPublicService {
    private final CompilationRepository compilationRepository;
    private final EventFieldSet eventFieldSet;
    private final CompilationMapper compilationMapper;


    @Override
    public CompilationDto getCompilationById(Integer compId) {
        CompilationEntity compilationEntity = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id=%d was not found", compId)));

        List<EventEntity> events = compilationEntity.getEvents();

        eventFieldSet.setConfirmedRequests(events);
        eventFieldSet.setViews(events);

        CompilationDto compilationDto = compilationMapper.toDto(compilationEntity);

        return compilationDto;
    }

    @Override
    public List<CompilationDto> searchCompilations(Boolean pinned, Integer from, Integer size) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(Expressions.asBoolean(true).isTrue());

        if (pinned != null) {
            predicates.add(QCompilationEntity.compilationEntity.pinned.eq(pinned));
        }

        Predicate predicate = ExpressionUtils.allOf(predicates);

        Pageable pageable = Page.getPageForCompilations(from, size);

        List<CompilationEntity> compilationEntities = compilationRepository.findAll(predicate, pageable).toList();

        List<EventEntity> events = new ArrayList<>();

        compilationEntities.forEach(compilationEntity -> events.addAll(compilationEntity.getEvents()));

        eventFieldSet.setViews(events);
        eventFieldSet.setConfirmedRequests(events);

        List<CompilationDto> compilationDtos = compilationEntities.stream().map(compilationMapper::toDto)
                .collect(Collectors.toList());

        return compilationDtos;
    }
}
