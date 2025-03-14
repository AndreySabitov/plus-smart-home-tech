package ru.practicum.interaction.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.interaction.enums.ProductCategory;
import ru.practicum.interaction.enums.ProductState;
import ru.practicum.interaction.enums.QuantityState;

import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductDto {
    UUID productId;
    @NotBlank
    String productName;
    @NotBlank
    String description;
    String imageSrc;
    @NotNull
    QuantityState quantityState;
    @NotNull
    ProductState productState;
    ProductCategory productCategory;
    @Min(value = 1)
    @NotNull
    Float price;
}
