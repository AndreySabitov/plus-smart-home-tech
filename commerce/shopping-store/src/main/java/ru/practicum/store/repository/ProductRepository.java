package ru.practicum.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.store.model.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
    Boolean existsByProductName(String productName);
}
