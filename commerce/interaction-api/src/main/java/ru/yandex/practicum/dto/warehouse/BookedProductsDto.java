package ru.yandex.practicum.dto.warehouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookedProductsDto {
    Double deliveryWeight;
    Double deliveryVolume;
    Boolean fragile;
}
