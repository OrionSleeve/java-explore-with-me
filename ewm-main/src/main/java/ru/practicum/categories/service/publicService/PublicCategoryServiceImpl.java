package ru.practicum.categories.service.publicService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.mapper.CategoryMapper;
import ru.practicum.categories.model.CategoryEntity;
import ru.practicum.categories.repository.CategoryRepo;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PublicCategoryServiceImpl implements PublicCategoryService {
    private final CategoryRepo categoryRepo;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> getAllCategory(org.springframework.data.domain.Pageable pageCategories) {
        Page<CategoryEntity> allCategories = categoryRepo.findAll(pageCategories);
        return allCategories.stream().map(categoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Integer id) {
        CategoryEntity categoryEntity = categoryRepo.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Category not found", id)));

        return categoryMapper.toDto(categoryEntity);
    }
}
