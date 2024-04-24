package ru.practicum.categories.service.publicService;

import org.springframework.data.domain.Pageable;
import ru.practicum.categories.dto.CategoryDto;

import java.util.List;

public interface PublicCategoryService {
    List<CategoryDto> getAllCategory(Pageable pageCategories);

    CategoryDto getCategoryById(Integer id);
}
