package ru.practicum.cart.service;

import ru.practicum.interaction.dto.cart.ChangeProductQuantityRequest;
import ru.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.dto.store.ProductDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {
    ShoppingCartDto addProductsInCart(String username, Map<UUID, Long> products);

    ShoppingCartDto getActiveShoppingCartOfUser(String username);

    void deactivateCart(String username);

    ShoppingCartDto removeOtherProductsFromCart(String username, List<UUID> productIds);

    ProductDto changeProductQuantity(String username, ChangeProductQuantityRequest request);
}
