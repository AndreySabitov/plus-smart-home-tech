package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.order.CreateNewOrderRequest;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.exceptions.AuthorizationException;
import ru.practicum.feign_client.WarehouseClient;
import ru.practicum.mapper.OrderMapper;
import ru.practicum.repository.OrderRepository;

import java.util.List;

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

    private void checkUsername(String username) {
        if (username.isBlank()) {
            throw new AuthorizationException("Имя пользователя не должно быть пустым");
        }
    }
}
