package ru.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class AssemblyProductsForOrderRequest {
    @NotNull
    private UUID orderId;
    @NotNull
    private Map<UUID, Long> products;
}
