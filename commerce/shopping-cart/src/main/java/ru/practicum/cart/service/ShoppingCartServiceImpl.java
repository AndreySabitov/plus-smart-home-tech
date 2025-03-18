package ru.practicum.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.cart.exceptions.NoProductsInShoppingCartException;
import ru.practicum.cart.exceptions.NotAuthorizedUserException;
import ru.practicum.cart.exceptions.NotFoundShoppingCartException;
import ru.practicum.cart.mapper.ShoppingCartMapper;
import ru.practicum.cart.model.Cart;
import ru.practicum.cart.model.enums.ShoppingCartState;
import ru.practicum.cart.repository.ShoppingCartRepository;
import ru.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.practicum.dto.cart.ShoppingCartDto;
import ru.practicum.dto.store.ProductDto;
import ru.practicum.feign_client.WarehouseClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final WarehouseClient warehouseClient;

    @Override
    @Transactional
    public ShoppingCartDto addProductsInCart(String username, Map<UUID, Long> additionalProperties) {
        checkUsername(username);

        Optional<Cart> cartOpt = shoppingCartRepository.findByOwnerAndState(username, ShoppingCartState.ACTIVE);

        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cart.getAdditionalProperties().putAll(additionalProperties);
            return ShoppingCartMapper.mapToDto(cart);
        }

        ShoppingCartDto shoppingCartDto = ShoppingCartMapper.mapToDto(shoppingCartRepository.save(Cart.builder()
                .additionalProperties(additionalProperties)
                .state(ShoppingCartState.ACTIVE)
                .owner(username)
                .created(LocalDateTime.now())
                .build()));

        warehouseClient.checkProductsQuantity(shoppingCartDto);

        return shoppingCartDto;
    }

    @Override
    @Transactional
    public ShoppingCartDto getActiveShoppingCartOfUser(String username) {
        checkUsername(username);

        Optional<Cart> cartOpt = shoppingCartRepository
                .findByOwnerAndState(username, ShoppingCartState.ACTIVE);

        Cart result = cartOpt.orElseGet(() -> shoppingCartRepository.save(Cart.builder()
                .created(LocalDateTime.now())
                .owner(username)
                .state(ShoppingCartState.ACTIVE)
                .additionalProperties(new HashMap<>())
                .build()));

        return ShoppingCartMapper.mapToDto(result);
    }

    @Override
    @Transactional
    public void deactivateCart(String username) {
        checkUsername(username);

        Cart cart = shoppingCartRepository.findByOwnerAndState(username, ShoppingCartState.ACTIVE)
                .orElseThrow(() -> new NotFoundShoppingCartException("У данного пользователя нет активной корзины"));

        log.info("получили корзину: {}", cart);

        cart.setState(ShoppingCartState.DEACTIVATE);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProductsFromCart(String username, List<UUID> productIds) {
        checkUsername(username);

        Cart cart = shoppingCartRepository.findByOwnerAndState(username, ShoppingCartState.ACTIVE)
                .orElseThrow(() -> new NotFoundShoppingCartException("У данного пользователя нет активной корзины"));

        Map<UUID, Long> oldProducts = cart.getAdditionalProperties();


        if (!productIds.stream().allMatch(oldProducts::containsKey)) {
            throw new NoProductsInShoppingCartException("Таких продуктов нет в корзине");
        }

        Map<UUID, Long> newProducts = oldProducts.entrySet().stream()
                .filter(ap -> !productIds.contains(ap.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        cart.setAdditionalProperties(newProducts);


        return ShoppingCartMapper.mapToDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        Cart cart = shoppingCartRepository.findByOwnerAndState(username, ShoppingCartState.ACTIVE)
                .orElseThrow(() -> new NotFoundShoppingCartException("У данного пользователя нет активной корзины"));

        Map<UUID, Long> products = cart.getAdditionalProperties();

        if (!products.containsKey(request.getProductId())) {
            throw new NoProductsInShoppingCartException("Такого продукта нет в корзине");
        }

        products.put(request.getProductId(), request.getNewQuantity());

        return ShoppingCartMapper.mapToDto(cart);
    }

    private void checkUsername(String username) {
        if (username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }
    }
}
