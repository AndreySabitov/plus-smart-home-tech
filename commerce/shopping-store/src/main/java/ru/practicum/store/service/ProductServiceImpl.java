package ru.practicum.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.dto.ProductDto;
import ru.practicum.store.exception.DuplicateException;
import ru.practicum.store.mapper.ProductMapper;
import ru.practicum.store.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductDto addProduct(ProductDto productDto) {
        if (!productRepository.existsByProductName(productDto.getProductName())) {
            return ProductMapper.mapToDto(productRepository.save(ProductMapper.mapToProduct(productDto)));
        } else {
            throw new DuplicateException("Товар с таким именем уже существует");
        }
    }
}
