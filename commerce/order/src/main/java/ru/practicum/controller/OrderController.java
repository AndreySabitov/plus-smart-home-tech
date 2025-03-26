package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.order.CreateNewOrderRequest;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.order.ProductReturnRequest;
import ru.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

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

    @GetMapping
    public List<OrderDto> getOrdersOfUser(@RequestParam String username,
                                          @RequestParam(defaultValue = "0") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size) {
        return orderService.getOrdersOfUser(username, page, size);
    }

    @PostMapping("/return")
    public OrderDto returnOrder(@Valid @RequestBody ProductReturnRequest returnRequest) {
        return orderService.returnOrder(returnRequest);
    }

    @PostMapping("/payment")
    public OrderDto payOrder(@NotNull @RequestBody UUID orderId) {
        return orderService.payOrder(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto payOrderFailed(@NotNull @RequestBody UUID orderId) {
        return orderService.changeStateToPaymentFailed(orderId);
    }

    @PostMapping("/delivery")
    public OrderDto sendOrderToDelivery(@NotNull @RequestBody UUID orderId) {
        return orderService.sendOrderToDelivery(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto changeStateToDeliveryFailed(@NotNull @RequestBody UUID orderId) {
        return orderService.changeStateToDeliveryFailed(orderId);
    }
}
