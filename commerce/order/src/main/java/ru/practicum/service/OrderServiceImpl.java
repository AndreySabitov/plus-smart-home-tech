package ru.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.order.CreateNewOrderRequest;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.order.ProductReturnRequest;
import ru.practicum.dto.warehouse.BookedProductsDto;
import ru.practicum.enums.order.OrderState;
import ru.practicum.exceptions.AuthorizationException;
import ru.practicum.exceptions.NoOrderFoundException;
import ru.practicum.exceptions.NoProductInOrderException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.feign_client.WarehouseClient;
import ru.practicum.feign_client.exception.shopping_cart.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.practicum.feign_client.exception.warehouse.ProductNotFoundInWarehouseException;
import ru.practicum.mapper.OrderMapper;
import ru.practicum.model.Order;
import ru.practicum.repository.OrderRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WarehouseClient warehouseClient;

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest createOrderRequest, String username) {
        checkUsername(username);
        try {
            BookedProductsDto bookedProducts = warehouseClient
                    .checkProductsQuantity(createOrderRequest.getShoppingCart());

            log.info("Сохраняем информацию о заказе в БД");
            return OrderMapper.mapToDto(orderRepository.save(OrderMapper
                    .mapToOrder(createOrderRequest, username, bookedProducts)));
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ProductNotFoundInWarehouseException(e.getMessage());
            } else if (e.status() == 400) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(e.getMessage());
            } else {
                throw e;
            }
        }
    }

    @Override
    public List<OrderDto> getOrdersOfUser(String username, Integer page, Integer size) {
        checkUsername(username);

        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");

        PageRequest pageRequest = PageRequest.of(page, size, sortByCreated);

        return orderRepository.findAllByOwner(username, pageRequest).stream()
                .map(OrderMapper::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto returnOrder(ProductReturnRequest returnRequest) {
        Order oldOrder = getOrder(returnRequest.getOrderId());

        if (oldOrder.getState() == OrderState.PRODUCT_RETURNED || oldOrder.getState() == OrderState.CANCELED) {
            throw new ValidationException("Заказ уже был возвращён или отменён");
        }

        Map<UUID, Long> orderProducts = oldOrder.getProducts();
        Map<UUID, Long> returnProducts = returnRequest.getProducts();

        if (!orderProducts.keySet().stream()
                .filter(p -> !returnProducts.containsKey(p))
                .toList().isEmpty()) {
            throw new NoProductInOrderException("Не все продукты предоставлены для возврата");
        }
        orderProducts.forEach((key, value) -> {
            if (!Objects.equals(value, returnProducts.get(key))) {
                throw new ValidationException("Не совпадает количество товара с id = %s для возврата"
                        .formatted(key));
            }
        });


        oldOrder.setState(OrderState.PRODUCT_RETURNED);

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto payOrder(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        oldOrder.setState(OrderState.ON_PAYMENT);

        // тут будет логика по оплате заказа в сервисе payment

        oldOrder.setState(OrderState.PAID);

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto changeStateToPaymentFailed(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        // возможно нужно проверить статус

        oldOrder.setState(OrderState.PAYMENT_FAILED);

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto sendOrderToDelivery(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        // тут будет логика по обработке заказа сервисом delivery

        oldOrder.setState(OrderState.ON_DELIVERY);

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto changeStateToDeliveryFailed(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        oldOrder.setState(OrderState.DELIVERY_FAILED);

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto changeStateToCompleted(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        oldOrder.setState(OrderState.COMPLETED);

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto calculateOrderTotalPrice(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        // тут логика по расчёту стоимости всех товаров

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto calculateOrderDeliveryPrice(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        // тут логика по расчёту стоимости доставки

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto sendOrderToAssembly(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        // логика по сборке: вычесть товары со склада в сервисе warehouse?

        oldOrder.setState(OrderState.ASSEMBLED);

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto changeOrderStateToAssemblyFailed(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        oldOrder.setState(OrderState.ASSEMBLY_FAILED);

        return OrderMapper.mapToDto(oldOrder);
    }

    private void checkUsername(String username) {
        if (username.isBlank()) {
            throw new AuthorizationException("Имя пользователя не должно быть пустым");
        }
    }

    private Order getOrder(UUID id) {
        return orderRepository.findById(id).orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));
    }
}
