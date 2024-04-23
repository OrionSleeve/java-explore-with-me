package ru.practicum.categories.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Integer id;
    @NotBlank
    @Size(max = 50)
    private String name;
}