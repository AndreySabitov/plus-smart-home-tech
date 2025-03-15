package ru.practicum.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.dto.ProductDto;
import ru.practicum.interaction.dto.SetProductQuantityStateRequest;
import ru.practicum.interaction.enums.ProductCategory;
import ru.practicum.interaction.enums.QuantityState;
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
    public List<ProductDto> getProductsByType(@RequestParam ProductCategory category, @RequestParam Integer page,
                                              @RequestParam Integer size, @RequestParam String sort) {
        return productService.getProductsByType(category, page, size, sort);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return productService.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public Boolean deleteProduct(@RequestBody UUID productId) {
        log.info("Удаляем товар с id = {}", productId);
        return productService.deleteProduct(productId);
    }

    @PostMapping("/quantityState")
    public Boolean setQuantityState(@RequestParam UUID productId,
                                    @RequestParam(required = false) QuantityState quantityState) {
        return productService.setProductQuantityState(SetProductQuantityStateRequest.builder()
                .productId(productId)
                .quantityState(quantityState)
                .build());
    }

    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable UUID productId) {
        return productService.getProductById(productId);
    }
}
