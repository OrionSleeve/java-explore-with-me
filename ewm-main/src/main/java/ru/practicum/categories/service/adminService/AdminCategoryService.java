package ru.practicum.categories.service.adminService;

import ru.practicum.categories.dto.CategoryDto;

public interface AdminCategoryService {
    CategoryDto create(CategoryDto categoryDto);

    void removeCategory(Integer id);

    CategoryDto update(Integer id, CategoryDto categoryDto);
}
