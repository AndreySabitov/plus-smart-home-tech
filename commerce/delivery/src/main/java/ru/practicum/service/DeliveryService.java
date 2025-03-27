package ru.practicum.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.dto.delivery.DeliveryDto;
import ru.practicum.dto.order.OrderDto;

import java.util.UUID;

public interface DeliveryService {
    DeliveryDto createNewDelivery(DeliveryDto deliveryDto);

    Double calculateDeliveryCost(OrderDto orderDto);

    void changeStateToInProgress(UUID deliveryId);

    void changeStateToDelivered(UUID deliveryId);

    void changeStateToFailed(UUID deliveryId);
}
