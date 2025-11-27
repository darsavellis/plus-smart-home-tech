package ru.yandex.practicum.store.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.ProductState;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.store.mapper.ProductMapper;
import ru.yandex.practicum.store.model.Product;
import ru.yandex.practicum.store.repository.ProductRepository;
import ru.yandex.practicum.store.service.ShoppingStoreService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    final ProductRepository productRepository;

    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        log.info("Запрос на получение списка продуктов категории: {}, страница: {}, размер: {}",
            category, pageable.getPageNumber(), pageable.getPageSize());
        Page<ProductDto> productPage = productRepository.findAllByProductCategory(category, pageable).map(ProductMapper::toProductDto);
        log.info("Найдено {} продуктов категории {}", productPage.getTotalElements(), category);
        log.debug("Список продуктов: {}", productPage.getContent());
        return productPage;
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        log.info("Запрос на получение продукта с ID: {}", productId);
        Product foundProduct = productRepository.findById(productId)
            .orElseThrow(() -> {
                log.warn("Продукт с ID {} не найден", productId);
                return new NotFoundException("Product with %id doesn't exist");
            });
        log.debug("Найден продукт: {}", foundProduct);
        return ProductMapper.toProductDto(foundProduct);
    }

    public ProductDto createProduct(ProductDto newProductDto) {
        log.info("Запрос на создание нового продукта: {}", newProductDto);
        Product productToSave = ProductMapper.toProduct(newProductDto);
        log.debug("Преобразованная модель продукта перед сохранением: {}", productToSave);
        Product createdProduct = productRepository.save(productToSave);
        log.info("Продукт успешно создан с ID: {}", createdProduct.getProductId());
        return ProductMapper.toProductDto(createdProduct);
    }

    public ProductDto updateProduct(ProductDto updatedProductDto) {
        log.info("Запрос на обновление продукта с ID: {}", updatedProductDto.getProductId());
        log.debug("Данные для обновления: {}", updatedProductDto);

        Product existingProduct = productRepository.findById(updatedProductDto.getProductId())
            .orElseThrow(() -> {
                log.warn("Продукт с ID {} не найден для обновления", updatedProductDto.getProductId());
                return new NotFoundException("Product with %id doesn't exist");
            });

        log.debug("Текущее состояние продукта перед обновлением: {}", existingProduct);

        existingProduct.setProductName(updatedProductDto.getProductName());
        existingProduct.setDescription(updatedProductDto.getDescription());
        existingProduct.setImageSrc(updatedProductDto.getImageSrc());
        existingProduct.setQuantityState(updatedProductDto.getQuantityState());
        existingProduct.setProductState(updatedProductDto.getProductState());
        existingProduct.setProductCategory(updatedProductDto.getProductCategory());
        existingProduct.setPrice(updatedProductDto.getPrice());

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Продукт с ID {} успешно обновлен", existingProduct.getProductId());
        log.debug("Обновленный продукт: {}", updatedProduct);

        return ProductMapper.toProductDto(updatedProduct);
    }

    public ProductDto removeProductFromStore(UUID productId) {
        log.info("Запрос на удаление продукта с ID: {}", productId);

        Product productToDeactivate = productRepository.findById(productId)
            .orElseThrow(() -> {
                log.warn("Продукт с ID {} не найден для удаления", productId);
                return new NotFoundException("Product with ID " + productId + " doesn't exist");
            });

        log.debug("Найден продукт для удаления: {}", productToDeactivate);

        productToDeactivate.setProductState(ProductState.DEACTIVATE);
        log.debug("Состояние продукта изменено на: {}", ProductState.DEACTIVATE);

        Product deactivatedProduct = productRepository.save(productToDeactivate);
        log.info("Продукт с ID {} успешно удален (деактивирован)", productId);

        return ProductMapper.toProductDto(deactivatedProduct);
    }

    public ProductDto setQuantityState(SetProductQuantityStateRequest quantityUpdateRequest) {
        UUID productId = quantityUpdateRequest.getProductId();
        log.info("Запрос на изменение состояния количества продукта с ID: {}. Новое состояние: {}",
            productId, quantityUpdateRequest.getQuantityState());

        Product targetProduct = productRepository.findById(productId)
            .orElseThrow(() -> {
                log.warn("Продукт с ID {} не найден для изменения состояния количества", productId);
                return new NotFoundException("Product with ID " + productId + " doesn't exist");
            });

        log.debug("Текущее состояние количества: {}", targetProduct.getQuantityState());
        targetProduct.setQuantityState(quantityUpdateRequest.getQuantityState());
        log.debug("Новое состояние количества: {}", targetProduct.getQuantityState());

        Product productWithUpdatedQuantity = productRepository.save(targetProduct);
        log.info("Состояние количества продукта с ID {} успешно изменено", productId);

        return ProductMapper.toProductDto(productWithUpdatedQuantity);
    }
}
