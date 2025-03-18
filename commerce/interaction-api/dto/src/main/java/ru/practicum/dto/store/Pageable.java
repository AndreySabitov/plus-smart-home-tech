package ru.practicum.dto.store;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Setter
public class Pageable {
    private Integer page;
    private Integer size;
    private String sort;
}
