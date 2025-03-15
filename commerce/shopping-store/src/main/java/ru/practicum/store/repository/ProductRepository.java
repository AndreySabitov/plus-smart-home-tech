package ru.practicum.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.store.model.Product;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
