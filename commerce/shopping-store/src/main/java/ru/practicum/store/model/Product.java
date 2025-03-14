package ru.practicum.store.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.interaction.enums.ProductCategory;
import ru.practicum.interaction.enums.ProductState;
import ru.practicum.interaction.enums.QuantityState;

import java.util.UUID;

@Entity
@Table(name = "products")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    UUID productId;

    @Column(name = "product_name")
    String productName;

    String description;

    @Column(name = "image_src")
    String imageSrc;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "quantity_state")
    QuantityState quantityState;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "product_state")
    ProductState productState;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "product_category")
    ProductCategory productCategory;

    Float price;
}
