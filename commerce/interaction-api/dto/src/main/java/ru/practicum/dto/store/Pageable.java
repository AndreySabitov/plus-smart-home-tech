package ru.practicum.dto.store;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Setter
public class Pageable {
    @Min(value = 0)
    @NotNull
    private Integer page;
    @Min(value = 1)
    @NotNull
    private Integer size;
    private List<String> sort;
}
