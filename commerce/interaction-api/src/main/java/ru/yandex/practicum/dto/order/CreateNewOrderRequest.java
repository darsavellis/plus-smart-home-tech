package ru.yandex.practicum.dto.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateNewOrderRequest {
    ShoppingCartDto shoppingCart;
    AddressDto deliveryAddress;
}
