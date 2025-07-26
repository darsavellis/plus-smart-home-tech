package ru.yandex.practicum.store.service;

import jakarta.ws.rs.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ProductCategory;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.ProductState;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.store.mappers.ProductMapper;
import ru.yandex.practicum.store.model.Product;
import ru.yandex.practicum.store.repository.ProductRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    final ProductRepository productRepository;

    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        return productRepository.findAllByProductCategory(category, pageable).map(ProductMapper::toProductDto);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with %id doesn't exist"));
        return ProductMapper.toProductDto(product);
    }

    public ProductDto createProduct(ProductDto productDto) {
        Product product = productRepository.save(ProductMapper.toProduct(productDto));
        return ProductMapper.toProductDto(product);
    }

    public ProductDto updateProduct(ProductDto productDto) {
        Product product = productRepository.findById(productDto.getProductId())
                .orElseThrow(() -> new NotFoundException("Product with %id doesn't exist"));
        product.setProductName(productDto.getProductName());
        product.setDescription(productDto.getDescription());
        product.setImageSrc(productDto.getImageSrc());
        product.setQuantityState(productDto.getQuantityState());
        product.setProductState(productDto.getProductState());
        product.setProductCategory(productDto.getProductCategory());
        product.setPrice(productDto.getPrice());
        return ProductMapper.toProductDto(product);
    }

    public ProductDto removeProductFromStore(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow();
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        return ProductMapper.toProductDto(product);
    }

    public ProductDto setQuantityState(SetProductQuantityStateRequest setProductQuantityStateRequest) {
        Product product = productRepository.findById(setProductQuantityStateRequest.getProductId())
                .orElseThrow();
        product.setQuantityState(setProductQuantityStateRequest.getQuantityState());
        productRepository.save(product);
        return ProductMapper.toProductDto(product);
    }
}
