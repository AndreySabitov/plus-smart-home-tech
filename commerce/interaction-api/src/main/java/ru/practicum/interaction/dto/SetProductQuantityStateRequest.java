package ru.practicum.interaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.interaction.enums.QuantityState;

import java.util.UUID;

@Getter
@Builder
public class SetProductQuantityStateRequest {
    private UUID productId;
    private QuantityState quantityState;
}
