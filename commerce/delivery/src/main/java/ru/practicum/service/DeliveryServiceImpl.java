package ru.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.delivery.DeliveryDto;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.warehouse.AddressDto;
import ru.practicum.dto.warehouse.ShippedToDeliveryRequest;
import ru.practicum.enums.delivery.DeliveryState;
import ru.practicum.exceptions.NoDeliveryFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.feign_client.OrderClient;
import ru.practicum.feign_client.WarehouseClient;
import ru.practicum.feign_client.exception.warehouse.OrderBookingNotFoundException;
import ru.practicum.mapper.AddressMapper;
import ru.practicum.mapper.DeliveryMapper;
import ru.practicum.model.Address;
import ru.practicum.model.Delivery;
import ru.practicum.repository.AddressRepository;
import ru.practicum.repository.DeliveryRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final AddressRepository addressRepository;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Override
    @Transactional
    public DeliveryDto createNewDelivery(DeliveryDto deliveryDto) {
        Address fromAddress = addressRepository.save(AddressMapper.mapToAddress(deliveryDto.getFromAddress()));
        Address toAddress = addressRepository.save(AddressMapper.mapToAddress(deliveryDto.getToAddress()));

        return DeliveryMapper.mapToDto(deliveryRepository.save(DeliveryMapper.mapToDelivery(deliveryDto, fromAddress,
                toAddress)));
    }

    @Override
    @Transactional
    public Double calculateDeliveryCost(OrderDto orderDto) {
        UUID deliveryId = orderDto.getDeliveryId();
        if (deliveryId == null) {
            throw new ValidationException("Не задан deliveryId");
        }

        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setDeliveryWeight(orderDto.getDeliveryWeight());
        delivery.setDeliveryVolume(orderDto.getDeliveryVolume());
        delivery.setFragile(orderDto.getFragile());

        double baseCost = 5.0;
        AddressDto warehouseAddress = AddressMapper.mapToDto(delivery.getFromAddress());
        if (warehouseAddress.getStreet().equals("ADDRESS_2")) {
            baseCost = baseCost + baseCost * 2;
        } else if (warehouseAddress.getStreet().equals("ADDRESS_1")) {
            baseCost = baseCost * 2;
        }
        if (orderDto.getFragile()) {
            baseCost = baseCost + baseCost * 0.2;
        }
        baseCost = baseCost + orderDto.getDeliveryWeight() * 0.3;
        baseCost = baseCost + orderDto.getDeliveryVolume() * 0.2;
        if (!delivery.getToAddress().getStreet().equals(warehouseAddress.getStreet())) {
            baseCost = baseCost + baseCost * 0.2;
        }

        return baseCost;
    }

    @Override
    @Transactional
    public void changeStateToInProgress(UUID deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);

        try {
            warehouseClient.shipProductsToDelivery(ShippedToDeliveryRequest.builder()
                    .deliveryId(deliveryId)
                    .orderId(delivery.getOrderId())
                    .build());
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new OrderBookingNotFoundException(e.getMessage());
            }
        }

        delivery.setState(DeliveryState.IN_PROGRESS);
    }

    @Override
    @Transactional
    public void changeStateToDelivered(UUID deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);

        delivery.setState(DeliveryState.DELIVERED);

        //сменить статус заказа на DELIVERED в сервисе order
    }

    @Override
    public void changeStateToFailed(UUID deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);

        delivery.setState(DeliveryState.FAILED);

        orderClient.changeStateToDeliveryFailed(delivery.getOrderId());
    }

    private Delivery getDeliveryById(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставки с id = %s не существует"
                        .formatted(deliveryId)));
    }
}
