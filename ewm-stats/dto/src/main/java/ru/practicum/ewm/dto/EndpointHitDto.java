package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.ewm.Constants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {
    private Integer id;
    @NotBlank
    @Size(max = 256)
    private String app;
    @NotBlank
    @Size(max = 256)
    private String uri;
    @NotBlank
    @Size(max = 16)
    private String ip;
    @NotNull
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private LocalDateTime timestamp;
}
