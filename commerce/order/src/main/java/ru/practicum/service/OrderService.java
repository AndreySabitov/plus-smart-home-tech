package ru.practicum.service;

import ru.practicum.dto.order.CreateNewOrderRequest;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.order.ProductReturnRequest;

import java.util.List;

public interface OrderService {
    OrderDto createNewOrder(CreateNewOrderRequest createOrderRequest, String username);

    List<OrderDto> getOrdersOfUser(String username, Integer page, Integer size);

    OrderDto returnOrder(ProductReturnRequest returnRequest);
}
