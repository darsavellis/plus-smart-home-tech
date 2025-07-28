package ru.yandex.practicum.store.controller;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contract.shopping.store.ShoppingStoreContract;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.store.service.impl.ShoppingStoreServiceImpl;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingStoreController implements ShoppingStoreContract {
    final ShoppingStoreServiceImpl shoppingStoreService;

    @GetMapping
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable) {
        return shoppingStoreService.getProducts(category, pageable);
    }

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId) {
        return shoppingStoreService.getProduct(productId);
    }

    @PutMapping
    public ProductDto createProduct(@Validated @RequestBody ProductDto productDto) {
        return shoppingStoreService.createProduct(productDto);
    }

    @PostMapping
    public ProductDto updateProduct(@Validated @RequestBody ProductDto productDto) {
        return shoppingStoreService.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public ProductDto removeProductFromStore(@Validated @RequestBody UUID productId) {
        return shoppingStoreService.removeProductFromStore(productId);
    }

    @PostMapping("/quantityState")
    public void setQuantityState(@Validated @ModelAttribute SetProductQuantityStateRequest setProductQuantityStateRequest) {
        shoppingStoreService.setQuantityState(setProductQuantityStateRequest);
    }
}
