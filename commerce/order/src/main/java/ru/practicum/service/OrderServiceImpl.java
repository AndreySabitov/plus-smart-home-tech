package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.order.CreateNewOrderRequest;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.order.ProductReturnRequest;
import ru.practicum.enums.order.OrderState;
import ru.practicum.exceptions.AuthorizationException;
import ru.practicum.exceptions.NoOrderFoundException;
import ru.practicum.exceptions.NoProductInOrderException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.feign_client.WarehouseClient;
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
        //   try {
        //       BookedProductsDto bookedProducts =
        //               warehouseClient.checkProductsQuantity(createOrderRequest.getShoppingCart());

        log.info("Сохраняем информацию о заказе в БД");
        return OrderMapper.mapToDto(orderRepository.save(OrderMapper.mapToOrder(createOrderRequest, username)));
        //    } catch (FeignException e) {
        //        if (e.status() == 404) {
        //             throw new ProductNotFoundInWarehouseException(e.getMessage());
        //        } else if (e.status() == 400) {
        //            throw new ProductInShoppingCartLowQuantityInWarehouseException(e.getMessage());
        //        } else {
        //            throw e;
        //        }
        //     }
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
        Order oldOrder = orderRepository.findById(returnRequest.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));

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

    private void checkUsername(String username) {
        if (username.isBlank()) {
            throw new AuthorizationException("Имя пользователя не должно быть пустым");
        }
    }
}
