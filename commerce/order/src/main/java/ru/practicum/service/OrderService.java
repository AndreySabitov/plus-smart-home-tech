package ru.practicum.service;

import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.order.CreateNewOrderRequest;

import java.util.List;

public interface OrderService {
    OrderDto createNewOrder(CreateNewOrderRequest createOrderRequest, String username);

    List<OrderDto> getOrdersOfUser(String username, Integer page, Integer size);
}
