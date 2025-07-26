package ru.yandex.practicum.store.mappers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.store.model.Product;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductMapper {
    public Product toProduct(ProductDto productDto) {
        Product product = new Product();
        product.setProductId(productDto.getProductId());
        product.setProductName(productDto.getProductName());
        product.setDescription(productDto.getDescription());
        product.setImageSrc(productDto.getImageSrc());
        product.setQuantityState(productDto.getQuantityState());
        product.setProductState(productDto.getProductState());
        product.setProductCategory(productDto.getProductCategory());
        product.setPrice(productDto.getPrice());
        return product;
    }

    public ProductDto toProductDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setProductId(product.getProductId());
        productDto.setProductName(product.getProductName());
        productDto.setDescription(product.getDescription());
        productDto.setImageSrc(product.getImageSrc());
        productDto.setQuantityState(product.getQuantityState());
        productDto.setProductState(product.getProductState());
        productDto.setProductCategory(product.getProductCategory());
        productDto.setPrice(product.getPrice());
        return productDto;
    }
}
