package ru.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ShippedToDeliveryRequest {
    @NotNull
    private UUID orderId;
    @NotNull
    private UUID deliveryId;
}
