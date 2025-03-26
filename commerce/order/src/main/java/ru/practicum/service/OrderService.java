package ru.practicum.service;

import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.order.CreateNewOrderRequest;

public interface OrderService {
    OrderDto createNewOrder(CreateNewOrderRequest createOrderRequest, String username);
}
