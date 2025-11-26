package ru.yandex.practicum.dto.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.store.ProductDto;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductReturnRequest {
    String uuid;
    Set<ProductDto> products;
}
