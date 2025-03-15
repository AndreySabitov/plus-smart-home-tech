package ru.practicum.interaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import ru.practicum.interaction.enums.QuantityState;

import java.util.UUID;

@Getter
public class SetProductQuantityStateRequest {
    @NotNull
    private UUID productId;
    @NotNull
    private QuantityState quantityState;
}
