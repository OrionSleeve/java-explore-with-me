package ru.practicum.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class Page {
    public static Pageable getPageForCategories(Integer from, Integer size) {
        return PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
    }

    public static Pageable getPageForUsers(Integer from, Integer size) {
        return PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
    }

    public static Pageable getPageForEvents(Integer from, Integer size) {
        return PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
    }

    public static Pageable getPageForCompilations(Integer from, Integer size) {
        return PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
    }
}
