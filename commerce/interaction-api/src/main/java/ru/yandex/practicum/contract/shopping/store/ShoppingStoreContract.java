package ru.yandex.practicum.contract.shopping.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;

import java.util.UUID;

public interface ShoppingStoreContract {
    Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable);

    ProductDto getProduct(@PathVariable UUID productId);

    ProductDto createProduct(@Validated @RequestBody ProductDto productDto);

    ProductDto updateProduct(@Validated @RequestBody ProductDto productDto);

    ProductDto removeProductFromStore(@Validated @RequestBody UUID productId);

    void setQuantityState(@Validated @ModelAttribute SetProductQuantityStateRequest setProductQuantityStateRequest);
}
