package ru.practicum.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.cart.model.Cart;
import ru.practicum.cart.model.enums.ShoppingCartState;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByOwnerAndState(String owner, ShoppingCartState state);

    Boolean existsByOwnerAndState(String owner, ShoppingCartState state);
}
