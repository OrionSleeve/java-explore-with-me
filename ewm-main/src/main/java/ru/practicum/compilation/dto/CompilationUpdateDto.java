package ru.practicum.compilation.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompilationUpdateDto {
    private Set<Integer> events;
    private Boolean pinned;
    @Size(min = 1, max = 50)
    private String title;
}
