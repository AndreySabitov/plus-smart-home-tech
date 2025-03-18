package ru.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.dto.warehouse.AddProductToWarehouseRequest;
import ru.practicum.interaction.dto.warehouse.AddressDto;
import ru.practicum.interaction.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.dto.warehouse.NewProductInWarehouseRequest;
import ru.practicum.warehouse.address.AddressManager;
import ru.practicum.warehouse.exceptions.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.practicum.warehouse.exceptions.ProductNotFoundInWarehouseException;
import ru.practicum.warehouse.exceptions.SpecifiedProductAlreadyInWarehouseException;
import ru.practicum.warehouse.mapper.WarehouseProductMapper;
import ru.practicum.warehouse.model.WarehouseProduct;
import ru.practicum.warehouse.repository.WarehouseRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository repository;

    @Override
    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest newProductRequest) {
        if (repository.existsById(newProductRequest.getProductId())) { // возможно нужно переделать на проверку по всем полям, если есть отличия то обновлять
            throw new SpecifiedProductAlreadyInWarehouseException("Такой продукт уже есть на складе");
        }

        repository.save(WarehouseProductMapper.mapToProduct(newProductRequest));
    }

    @Override
    public BookedProductsDto checkProductsQuantity(ShoppingCartDto shoppingCartDto) {
        return checkQuantity(shoppingCartDto);
    }

    @Override
    @Transactional
    public void addProductQuantity(AddProductToWarehouseRequest addProductQuantity) {
        WarehouseProduct product = repository.findById(addProductQuantity.getProductId())
                .orElseThrow(() -> new ProductNotFoundInWarehouseException("Такого товара нет на складе"));

        product.setQuantity(product.getQuantity() + addProductQuantity.getQuantity());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        String address = AddressManager.CURRENT_ADDRESS;
        return AddressDto.builder()
                .country(address)
                .city(address)
                .street(address)
                .house(address)
                .flat(address)
                .build();
    }

    private BookedProductsDto checkQuantity(ShoppingCartDto shoppingCartDto) {
        Map<UUID, Long> cartProducts = shoppingCartDto.getProducts();
        Set<UUID> cartProductIds = cartProducts.keySet();

        Map<UUID, WarehouseProduct> warehouseProducts = repository.findAllById(cartProductIds).stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        if (cartProductIds.size() != warehouseProducts.size()) {
            Set<UUID> productIds = warehouseProducts.keySet();
            cartProductIds.forEach(id -> {
                if (!productIds.contains(id)) {
                    throw new ProductNotFoundInWarehouseException(String.format("Товара с id = %s нет на складе", id));
                }
            });
        }

        cartProducts.forEach((key, value) -> {
            if (warehouseProducts.get(key).getQuantity() < value) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException
                        (String.format("Товара с id = %s не хватает на складе", key));
            }
        });

        return getBookedProducts(warehouseProducts.values(), cartProducts);
    }

    private BookedProductsDto getBookedProducts(Collection<WarehouseProduct> productList,
                                                Map<UUID, Long> cartProducts) {
        return BookedProductsDto.builder()
                .fragile(productList.stream().anyMatch(WarehouseProduct::getFragile))
                .deliveryWeight(productList.stream()
                        .mapToDouble(p -> p.getWeight() * cartProducts.get(p.getProductId()))
                        .sum())
                .deliveryVolume(productList.stream()
                        .mapToDouble(p ->
                                p.getWidth() * p.getHeight() * p.getDepth() * cartProducts.get(p.getProductId()))
                        .sum())
                .build();
    }
}
