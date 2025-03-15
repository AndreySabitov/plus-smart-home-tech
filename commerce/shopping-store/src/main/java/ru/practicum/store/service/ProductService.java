package ru.practicum.store.service;

import ru.practicum.interaction.dto.DeleteProductRequest;
import ru.practicum.interaction.dto.ProductDto;
import ru.practicum.interaction.dto.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDto addProduct(ProductDto productDto);

    List<ProductDto> getProductsByType();

    ProductDto updateProduct(ProductDto productDto);

    Boolean deleteProduct(DeleteProductRequest deleteProduct);

    Boolean setProductQuantityState(SetProductQuantityStateRequest setProductQuantityState);

    ProductDto getProductById(UUID productId);
}
