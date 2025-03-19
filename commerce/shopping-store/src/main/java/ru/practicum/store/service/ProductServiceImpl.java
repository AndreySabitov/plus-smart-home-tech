package ru.practicum.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.store.Pageable;
import ru.practicum.dto.store.ProductDto;
import ru.practicum.dto.store.SetProductQuantityStateRequest;
import ru.practicum.enums.ProductCategory;
import ru.practicum.enums.ProductState;
import ru.practicum.store.exception.ProductNotFoundException;
import ru.practicum.store.exception.ValidationException;
import ru.practicum.store.mapper.ProductMapper;
import ru.practicum.store.model.Product;
import ru.practicum.store.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductDto addProduct(ProductDto productDto) {
        log.info("Сохраняем новый товар в БД");
        return ProductMapper.mapToDto(productRepository.save(ProductMapper.mapToProduct(productDto)));
    }

    @Override
    public List<ProductDto> getProductsByType(ProductCategory category, Pageable pageable) {
        Sort pageSort = Sort.by(String.join(", ", pageable.getSort()));
        PageRequest pageRequest = PageRequest.of(pageable.getPage(), pageable.getSize(), pageSort);

        return productRepository.findAllByProductCategoryAndProductState(category, ProductState.ACTIVE,
                        pageRequest).stream()
                .map(ProductMapper::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        UUID uuid = productDto.getProductId();
        String newProductName = productDto.getProductName();
        String newDescription = productDto.getDescription();
        String newImageSrc = productDto.getImageSrc();
        ProductCategory newProductCategory = productDto.getProductCategory();
        Float newPrice = productDto.getPrice();

        if (uuid == null) {
            throw new ValidationException("id должен быть задан");
        }
        Product oldProduct = productRepository.findById(uuid)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден"));

        if (!newProductName.isBlank() && !newProductName.equals(oldProduct.getProductName())) {
            log.info("Обновляем имя");
            oldProduct.setProductName(newProductName);
        }
        if (!newDescription.isBlank() && !newDescription.equals(oldProduct.getDescription())) {
            log.info("Обновляем описание");
            oldProduct.setDescription(newDescription);
        }
        if (!newImageSrc.equals(oldProduct.getImageSrc())) {
            log.info("Обновляем ссылку на фото");
            oldProduct.setImageSrc(newImageSrc);
        }
        if (newProductCategory != null && !newProductCategory.equals(oldProduct.getProductCategory())) {
            log.info("Обновляем категорию");
            oldProduct.setProductCategory(newProductCategory);
        }
        if (newPrice != null && !newPrice.equals(oldProduct.getPrice())) {
            log.info("Обновляем цену");
            oldProduct.setPrice(newPrice);
        }

        return ProductMapper.mapToDto(oldProduct);
    }

    @Override
    @Transactional
    public Boolean deleteProduct(UUID productId) {
        Product oldProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));

        if (oldProduct.getProductState().equals(ProductState.DEACTIVATE)) {
            log.info("Товар уже деактивирован");
            return false;
        }

        log.info("Деактивация товара с id = {}", productId);
        oldProduct.setProductState(ProductState.DEACTIVATE);

        return true;
    }

    @Override
    @Transactional
    public Boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        Product oldProduct = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));

        if (oldProduct.getQuantityState().equals(request.getQuantityState())) {
            log.info("Такое количество уже задано");
            return false;
        }

        oldProduct.setQuantityState(request.getQuantityState());

        return true;
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));

        return ProductMapper.mapToDto(product);
    }
}
