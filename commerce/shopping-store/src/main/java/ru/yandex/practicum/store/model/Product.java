package ru.yandex.practicum.store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.ProductCategory;
import ru.yandex.practicum.dto.ProductState;
import ru.yandex.practicum.dto.QuantityState;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @Column(name = "product_id", columnDefinition = "uuid", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID productId;

    @Column(name = "product_name")
    String productName;

    @Column(name = "description")
    String description;

    @Column(name = "image_src")
    String imageSrc;

    @Column(name = "quantity_state")
    @Enumerated(EnumType.STRING)
    QuantityState quantityState;

    @Column(name = "product_state")
    @Enumerated(EnumType.STRING)
    ProductState productState;

    @Column(name = "product_category")
    @Enumerated(EnumType.STRING)
    ProductCategory productCategory;

    @Column(name = "price")
    @Min(value = 1)
    Double price;
}
