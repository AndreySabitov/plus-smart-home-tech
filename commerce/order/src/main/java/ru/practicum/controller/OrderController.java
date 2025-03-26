package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.order.CreateNewOrderRequest;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.service.OrderService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    @PutMapping
    public OrderDto createNewOrder(@Valid @RequestBody CreateNewOrderRequest createOrderRequest,
                                   @RequestParam String username) {
        log.info("Запрос на добавление нового заказа {}", createOrderRequest);
        return orderService.createNewOrder(createOrderRequest, username);
    }
}
