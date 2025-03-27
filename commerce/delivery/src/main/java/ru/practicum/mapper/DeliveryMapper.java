package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.delivery.DeliveryDto;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.enums.delivery.DeliveryState;
import ru.practicum.model.Address;
import ru.practicum.model.Delivery;

@UtilityClass
public class DeliveryMapper {

    public Delivery mapToDelivery(DeliveryDto deliveryDto, OrderDto orderDto, Address fromAddress, Address toAddress) {
        return Delivery.builder()
                .state(DeliveryState.CREATED)
                .deliveryWeight(orderDto.getDeliveryWeight())
                .deliveryVolume(orderDto.getDeliveryVolume())
                .fromAddress(fromAddress)
                .orderId(deliveryDto.getOrderId())
                .fragile(orderDto.getFragile())
                .toAddress(toAddress)
                .build();
    }

    public DeliveryDto mapToDto(Delivery delivery) {
        return DeliveryDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .fromAddress(AddressMapper.mapToDto(delivery.getFromAddress()))
                .orderId(delivery.getOrderId())
                .state(delivery.getState())
                .toAddress(AddressMapper.mapToDto(delivery.getToAddress()))
                .build();
    }
}
