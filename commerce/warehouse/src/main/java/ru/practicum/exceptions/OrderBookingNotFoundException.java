package ru.practicum.exceptions;

public class OrderBookingNotFoundException extends RuntimeException {
    public OrderBookingNotFoundException(String message) {
        super(message);
    }
}
