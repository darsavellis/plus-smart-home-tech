package ru.yandex.practicum.store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;

import java.util.UUID;

public interface ShoppingStoreService {
    Page<ProductDto> getProducts(ProductCategory category, Pageable pageable);

    ProductDto getProduct(UUID productId);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    ProductDto removeProductFromStore(UUID productId);

    ProductDto setQuantityState(SetProductQuantityStateRequest setProductQuantityStateRequest);
}
