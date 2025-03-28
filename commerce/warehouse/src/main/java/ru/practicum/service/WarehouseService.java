package ru.practicum.service;

import ru.practicum.dto.cart.ShoppingCartDto;
import ru.practicum.dto.warehouse.*;

public interface WarehouseService {
    void addNewProduct(NewProductInWarehouseRequest newProductRequest);

    BookedProductsDto checkProductsQuantity(ShoppingCartDto shoppingCartDto);

    void addProductQuantity(AddProductToWarehouseRequest addProductQuantity);

    AddressDto getWarehouseAddress();

    BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest assemblyRequest);
}
