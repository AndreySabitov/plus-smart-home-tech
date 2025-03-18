package ru.practicum.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.cart.service.ShoppingCartService;
import ru.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.practicum.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PutMapping
    public ShoppingCartDto addProductsInCart(@RequestParam String username,
                                             @RequestBody Map<UUID, Long> additionalProperties) {
        log.info("Получили: username = {}; additionalProperties = {}", username, additionalProperties);

        return shoppingCartService.addProductsInCart(username, additionalProperties);
    }

    @GetMapping
    public ShoppingCartDto getActiveShoppingCartOfUser(@RequestParam String username) {
        return shoppingCartService.getActiveShoppingCartOfUser(username);
    }

    @DeleteMapping
    public void deactivateCart(@RequestParam String username) {
        shoppingCartService.deactivateCart(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeOtherProductsFromCart(@RequestParam String username,
                                                       @RequestBody List<UUID> productIds) {
        return shoppingCartService.removeProductsFromCart(username, productIds);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(@RequestParam String username,
                                                 @Valid @RequestBody ChangeProductQuantityRequest request) {
        return shoppingCartService.changeProductQuantity(username, request);
    }
}
