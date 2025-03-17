package ru.practicum.interaction.dto.store;

import lombok.*;
import ru.practicum.interaction.enums.QuantityState;

import java.util.UUID;

@Getter
@Builder
public class SetProductQuantityStateRequest {
    private UUID productId;
    private QuantityState quantityState;
}
