package ru.practicum.categories.service.adminService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.mapper.CategoryMapper;
import ru.practicum.categories.model.CategoryEntity;
import ru.practicum.categories.repository.CategoryRepo;
import ru.practicum.exception.NotFoundException;


@Service
@Transactional
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepo categoryRepo;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto create(CategoryDto categoryDto) {
        CategoryEntity createdCategory = categoryRepo.save(categoryMapper.toEntity(categoryDto));
        return categoryMapper.toDto(createdCategory);
    }

    @Override
    public void removeCategory(Integer id) {
        categoryRepo.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Category with id=%d was not found", id)));
        categoryRepo.deleteById(id);
    }

    @Override
    public CategoryDto update(Integer id, CategoryDto categoryDto) {
        return null;
    }
}
