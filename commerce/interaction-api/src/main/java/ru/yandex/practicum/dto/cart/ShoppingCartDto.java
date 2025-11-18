package ru.yandex.practicum.dto.cart;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCartDto {
    UUID shoppingCartId;
    Map<UUID, Integer> products;
}
