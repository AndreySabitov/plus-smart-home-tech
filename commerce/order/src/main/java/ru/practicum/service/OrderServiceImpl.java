package ru.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.order.CreateNewOrderRequest;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.warehouse.BookedProductsDto;
import ru.practicum.feign_client.WarehouseClient;
import ru.practicum.feign_client.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.practicum.feign_client.exception.ProductNotFoundInWarehouseException;
import ru.practicum.mapper.OrderMapper;
import ru.practicum.repository.OrderRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WarehouseClient warehouseClient;

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest createOrderRequest, String username) {
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
}
