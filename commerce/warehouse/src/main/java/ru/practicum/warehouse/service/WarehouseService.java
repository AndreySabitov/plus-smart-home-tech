package ru.practicum.warehouse.service;

import ru.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.dto.warehouse.AddProductToWarehouseRequest;
import ru.practicum.interaction.dto.warehouse.AddressDto;
import ru.practicum.interaction.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.dto.warehouse.NewProductInWarehouseRequest;

public interface WarehouseService {
    void addNewProduct(NewProductInWarehouseRequest newProductRequest);

    BookedProductsDto checkProductsQuantity(ShoppingCartDto shoppingCartDto);

    void addProductQuantity(AddProductToWarehouseRequest addProductQuantity);

    AddressDto getWarehouseAddress();
}
