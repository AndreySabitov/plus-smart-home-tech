package ru.practicum.cart.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.cart.model.Cart;
import ru.practicum.interaction.dto.cart.ShoppingCartDto;

@UtilityClass
public class ShoppingCartMapper {

    public ShoppingCartDto mapToDto(Cart cart) {
        return ShoppingCartDto.builder()
                .shoppingCartId(cart.getShoppingCartId())
                .products(cart.getAdditionalProperties())
                .build();
    }
}
