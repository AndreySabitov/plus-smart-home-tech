package ru.practicum.store.service;

import ru.practicum.dto.store.Pageable;
import ru.practicum.dto.store.ProductDto;
import ru.practicum.dto.store.SetProductQuantityStateRequest;
import ru.practicum.enums.ProductCategory;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDto addProduct(ProductDto productDto);

    List<ProductDto> getProductsByType(ProductCategory category, Pageable pageable);

    ProductDto updateProduct(ProductDto productDto);

    Boolean deleteProduct(UUID productId);

    Boolean setProductQuantityState(SetProductQuantityStateRequest setProductQuantityState);

    ProductDto getProductById(UUID productId);
}
