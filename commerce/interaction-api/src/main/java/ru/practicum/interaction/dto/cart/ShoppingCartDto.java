package ru.practicum.interaction.dto.cart;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class ShoppingCartDto {
    private UUID shoppingCartId;
    private Map<UUID, Long> additionalProperties;
}
