package ru.practicum.store.service;

import ru.practicum.interaction.dto.store.ProductDto;
import ru.practicum.interaction.dto.store.SetProductQuantityStateRequest;
import ru.practicum.interaction.enums.ProductCategory;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDto addProduct(ProductDto productDto);

    List<ProductDto> getProductsByType(ProductCategory category, Integer page, Integer size, String sort);

    ProductDto updateProduct(ProductDto productDto);

    Boolean deleteProduct(UUID productId);

    Boolean setProductQuantityState(SetProductQuantityStateRequest setProductQuantityState);

    ProductDto getProductById(UUID productId);
}
