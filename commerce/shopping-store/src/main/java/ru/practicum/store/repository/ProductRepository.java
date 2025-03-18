package ru.practicum.store.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.enums.ProductCategory;
import ru.practicum.store.model.Product;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByProductCategory(ProductCategory category, PageRequest pageRequest);
}
