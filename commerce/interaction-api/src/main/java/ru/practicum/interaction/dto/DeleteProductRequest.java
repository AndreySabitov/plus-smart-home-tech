package ru.practicum.interaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteProductRequest {
    @NotNull
    private UUID productId;
}
