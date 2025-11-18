package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "product_in_warehouses")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductInWarehouse {
    @Id
    @Column(columnDefinition = "uuid")
    UUID productId;
    @Column(name = "fragile")
    Boolean fragile;
    @Embedded
    Dimension dimension;
    @Column(name = "weight")
    Double weight;
    @Column(name = "quantity")
    Long quantity = 0L;
}
