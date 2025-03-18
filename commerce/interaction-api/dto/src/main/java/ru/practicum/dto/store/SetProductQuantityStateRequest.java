package ru.practicum.dto.store;

import lombok.*;
import ru.practicum.enums.QuantityState;

import java.util.UUID;

@Getter
@Builder
public class SetProductQuantityStateRequest {
    private UUID productId;
    private QuantityState quantityState;
}
