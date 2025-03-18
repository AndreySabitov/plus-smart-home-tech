package ru.practicum.warehouse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.cart.ShoppingCartDto;
import ru.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.practicum.dto.warehouse.AddressDto;
import ru.practicum.dto.warehouse.BookedProductsDto;
import ru.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.practicum.warehouse.service.WarehouseService;

@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService service;

    @PutMapping
    public void addNewProduct(@Valid @RequestBody NewProductInWarehouseRequest newProductRequest) {
        service.addNewProduct(newProductRequest);
    }

    @PostMapping("/check")
    public BookedProductsDto checkProductsQuantity(@Valid @RequestBody ShoppingCartDto shoppingCartDto) {
        log.info("Поступил запрос на проверку наличия товаров из корзины");
        return service.checkProductsQuantity(shoppingCartDto);
    }

    @PostMapping("/add")
    public void addProductQuantity(@Valid @RequestBody AddProductToWarehouseRequest addProductQuantity) {
        service.addProductQuantity(addProductQuantity);
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return service.getWarehouseAddress();
    }
}
