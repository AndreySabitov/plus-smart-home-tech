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
    public ShoppingCartDto addProductsInCart(String username, Map<UUID, Long> newProducts) {
        checkUsername(username);
        log.info("Начало добавления товаров в активную корзину");
        Optional<Cart> cartOpt = shoppingCartRepository.findByOwnerAndState(username, ShoppingCartState.ACTIVE);

        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            log.info("Получили существующую корзину с товарами {}", cart.getCartProducts());
            cart.getCartProducts().putAll(newProducts);
            log.info("Добавили в неё новые товары и получили новый список товаров {}", cart.getCartProducts());
            return ShoppingCartMapper.mapToDto(cart);
        }

        ShoppingCartDto shoppingCartDto = ShoppingCartMapper.mapToDto(shoppingCartRepository.save(Cart.builder()
                .cartProducts(newProducts)
                .state(ShoppingCartState.ACTIVE)
                .owner(username)
                .created(LocalDateTime.now())
                .build()));

        log.info("Проверка наличия товаров {} на складе", shoppingCartDto.getProducts());
        warehouseClient.checkProductsQuantity(shoppingCartDto);
        log.info("Проверка наличия товаров прошла успешно");

        return shoppingCartDto;
    }

    @Override
    @Transactional
    public ShoppingCartDto getActiveShoppingCartOfUser(String username) {
        checkUsername(username);

        Optional<Cart> cartOpt = shoppingCartRepository
                .findByOwnerAndState(username, ShoppingCartState.ACTIVE);

        log.info("Возвращаем существующую активную корзину или создаём новую для пользователя {}", username);

        Cart result = cartOpt.orElseGet(() -> shoppingCartRepository.save(Cart.builder()
                .created(LocalDateTime.now())
                .owner(username)
                .state(ShoppingCartState.ACTIVE)
                .cartProducts(new HashMap<>())
                .build()));

        return ShoppingCartMapper.mapToDto(result);
    }

    @Override
    @Transactional
    public void deactivateCart(String username) {
        checkUsername(username);

        Cart cart = shoppingCartRepository.findByOwnerAndState(username, ShoppingCartState.ACTIVE)
                .orElseThrow(() -> new NotFoundShoppingCartException("У данного пользователя нет активной корзины"));

        log.info("получили корзину для деактивации: {}", cart.getShoppingCartId());

        cart.setState(ShoppingCartState.DEACTIVATE);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProductsFromCart(String username, List<UUID> productIds) {
        checkUsername(username);

        Cart cart = shoppingCartRepository.findByOwnerAndState(username, ShoppingCartState.ACTIVE)
                .orElseThrow(() -> new NotFoundShoppingCartException("У данного пользователя нет активной корзины"));

        Map<UUID, Long> oldProducts = cart.getCartProducts();


        if (!productIds.stream().allMatch(oldProducts::containsKey)) {
            throw new NoProductsInShoppingCartException("Таких продуктов нет в корзине");
        }

        Map<UUID, Long> newProducts = oldProducts.entrySet().stream()
                .filter(cp -> !productIds.contains(cp.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        cart.setCartProducts(newProducts);


        return ShoppingCartMapper.mapToDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        Cart cart = shoppingCartRepository.findByOwnerAndState(username, ShoppingCartState.ACTIVE)
                .orElseThrow(() -> new NotFoundShoppingCartException("У данного пользователя нет активной корзины"));

        Map<UUID, Long> products = cart.getCartProducts();

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
