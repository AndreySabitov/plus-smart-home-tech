package ru.practicum.feign_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.store.ProductDto;
import ru.practicum.enums.ProductCategory;
import ru.practicum.enums.QuantityState;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface StoreClient {
    @PutMapping
    ProductDto addProduct(@RequestBody ProductDto productDto);

    @GetMapping
    List<ProductDto> getProductsByType(@RequestParam ProductCategory category, @RequestParam Integer page,
                                       @RequestParam Integer size, @RequestParam String sort);

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    Boolean deleteProduct(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    Boolean setQuantityState(@RequestParam UUID productId, @RequestParam(required = false) QuantityState quantityState);

    @GetMapping("/{productId}")
    ProductDto getProductById(@PathVariable UUID productId);
}
