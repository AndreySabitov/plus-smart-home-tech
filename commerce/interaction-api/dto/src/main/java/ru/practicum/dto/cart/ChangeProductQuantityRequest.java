package ru.practicum.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ChangeProductQuantityRequest {
    @NotNull
    private UUID productId;
    @NotNull
    private Long newQuantity;
}
