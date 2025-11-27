package ru.yandex.practicum.contract.shopping.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;

import java.util.UUID;

public interface ShoppingStoreContract {
    @GetMapping
    Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);

    @PutMapping
    ProductDto createProduct(@Validated @RequestBody ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@Validated @RequestBody ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    ProductDto removeProductFromStore(@Validated @RequestBody UUID productId);

    @PostMapping("/quantityState")
    void setQuantityState(@Validated @ModelAttribute SetProductQuantityStateRequest setProductQuantityStateRequest);
}
