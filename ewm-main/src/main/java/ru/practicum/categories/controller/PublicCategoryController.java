package ru.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.publicService.PublicCategoryService;
import ru.practicum.util.Page;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class PublicCategoryController {
    private final PublicCategoryService publicCategoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAllCategory(@RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
                                            @RequestParam(defaultValue = "10", required = false) @Min(1) Integer size) {
        log.info("Received request to get all categories with pagination: from={}, size={}", from, size);
        Pageable pageForCategories = Page.getPageForCategories(from, size);
        return publicCategoryService.getAllCategory(pageForCategories);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable @NotNull @Min(1) Integer catId) {
        log.info("Received request to get category by ID: {}", catId);
        return publicCategoryService.getCategoryById(catId);
    }
}
