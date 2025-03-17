package ru.practicum.cart.exceptions;

public class NotFoundShoppingCartException extends RuntimeException {
    public NotFoundShoppingCartException(String message) {
        super(message);
    }
}
