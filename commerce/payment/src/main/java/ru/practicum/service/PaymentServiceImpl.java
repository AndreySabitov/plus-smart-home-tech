package ru.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.payment.PaymentDto;
import ru.practicum.dto.store.ProductDto;
import ru.practicum.exceptions.NoPaymentFoundException;
import ru.practicum.exceptions.NotEnoughInfoInOrderToCalculateException;
import ru.practicum.feign_client.StoreClient;
import ru.practicum.feign_client.exception.shopping_store.ProductNotFoundException;
import ru.practicum.mapper.PaymentMapper;
import ru.practicum.model.Payment;
import ru.practicum.model.enums.PaymentState;
import ru.practicum.repository.PaymentRepository;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final StoreClient storeClient;

    @Override
    @Transactional
    public PaymentDto makingPaymentForOrder(OrderDto orderDto) {
        if (orderDto.getProductPrice() == null || orderDto.getDeliveryPrice() == null ||
                orderDto.getTotalPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Для оплаты не хватает информации в заказе");
        }

        return PaymentMapper.mapToDto(paymentRepository.save(PaymentMapper.mapToPayment(orderDto)));
    }

    @Override
    public Double calculateProductsCost(OrderDto orderDto) {
        try {
            Map<UUID, Long> products = orderDto.getProducts();

            if (products.isEmpty()) {
                throw new NotEnoughInfoInOrderToCalculateException("Список продуктов не может быть пустым");
            }

            Map<UUID, Float> productsPrice = products.keySet().stream()
                    .map(storeClient::getProductById)
                    .collect(Collectors.toMap(ProductDto::getProductId, ProductDto::getPrice));

            return products.entrySet().stream()
                    .map(entry -> entry.getValue() * productsPrice.get(entry.getKey()))
                    .mapToDouble(Float::floatValue)
                    .sum();
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ProductNotFoundException("Продукт не найден");
            } else {
                throw e;
            }
        }
    }

    @Override
    public Double calculateTotalCost(OrderDto orderDto) {
        if (orderDto.getProductPrice() == null || orderDto.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Не посчитана цена продуктов в заказе или цена доставки");
        }

        return orderDto.getProductPrice() * 1.1 + orderDto.getDeliveryPrice();
    }

    @Override
    @Transactional
    public void changePaymentStateToSuccess(UUID paymentId) {
        Payment oldPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoPaymentFoundException("Сведений об оплате не найдено"));

        oldPayment.setState(PaymentState.SUCCESS);

        // вызвать изменение в сервисе заказов - статус оплачен
    }

    @Override
    @Transactional
    public void changePaymentStateToFailed(UUID paymentId) {
        Payment oldPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoPaymentFoundException("Сведений об оплате не найдено"));

        oldPayment.setState(PaymentState.FAILED);

        // вызвать изменение в сервисе заказов - оплата не прошла
    }
}
