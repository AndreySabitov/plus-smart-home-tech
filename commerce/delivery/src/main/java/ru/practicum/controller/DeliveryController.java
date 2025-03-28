package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.delivery.DeliveryDto;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.service.DeliveryService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto createNewDelivery(@Valid @RequestBody DeliveryDto deliveryDto) {
        return deliveryService.createNewDelivery(deliveryDto);
    }

    @PostMapping("/cost")
    public Double calculateDeliveryCost(@Valid @RequestBody OrderDto orderDto) {
        return deliveryService.calculateDeliveryCost(orderDto);
    }

    @PostMapping("/picked")
    public void changeStateToInProgress(@RequestBody UUID deliveryId) {
        deliveryService.changeStateToInProgress(deliveryId);
    }

    @PostMapping("/successful")
    public void changeStateToDelivered(@RequestBody UUID deliveryId) {
        deliveryService.changeStateToDelivered(deliveryId);
    }

    @PostMapping("/failed")
    public void changeStateToFailed(@RequestBody UUID deliveryId) {
        deliveryService.changeStateToFailed(deliveryId);
    }
}
