package ru.practicum.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.dto.DeleteProductRequest;
import ru.practicum.interaction.dto.ProductDto;
import ru.practicum.interaction.dto.SetProductQuantityStateRequest;
import ru.practicum.store.service.ProductService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PutMapping
    public ProductDto addProduct(@Valid @RequestBody ProductDto productDto) {
        return productService.addProduct(productDto);
    }

    @GetMapping
    public List<ProductDto> getProductsByType() { //отложил реализацию чтобы посмотреть пример запроса в постман тестах
        return productService.getProductsByType();
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return productService.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public Boolean deleteProduct(@RequestBody DeleteProductRequest deleteProduct) {
        log.info("Удаляем товар с id = {}", deleteProduct.getProductId());
        return productService.deleteProduct(deleteProduct);
    }

    @PostMapping("/quantityState")
    public Boolean setQuantityState(@Valid @RequestBody SetProductQuantityStateRequest setProductQuantityState) {
        return productService.setProductQuantityState(setProductQuantityState);
    }

    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable UUID productId) {
        return productService.getProductById(productId);
    }
}
