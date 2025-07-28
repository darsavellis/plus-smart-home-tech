package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewProductInWarehouseRequest {
    UUID productId;
    Boolean fragile;
    DimensionDto dimension;
    @Min(value = 1)
    Double weight;
    Long quantity;
}
