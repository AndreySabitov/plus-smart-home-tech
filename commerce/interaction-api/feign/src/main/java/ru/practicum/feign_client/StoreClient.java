package ru.practicum.feign_client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.store.ProductDto;
import ru.practicum.dto.store.SetProductQuantityStateRequest;
import ru.practicum.enums.QuantityState;

import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface StoreClient {
    @PutMapping
    ProductDto addProduct(@RequestBody ProductDto productDto) throws FeignException;

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto productDto) throws FeignException;

    @PostMapping("/removeProductFromStore")
    Boolean deleteProduct(@RequestBody UUID productId) throws FeignException;

    @PostMapping("/quantityState")
    Boolean setQuantityState(SetProductQuantityStateRequest request) throws FeignException;

    @GetMapping("/{productId}")
    ProductDto getProductById(@PathVariable UUID productId) throws FeignException;
}
