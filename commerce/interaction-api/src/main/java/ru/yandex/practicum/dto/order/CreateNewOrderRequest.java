package ru.yandex.practicum.dto.order;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateNewOrderRequest {
    ShoppingCartDto shoppingCart;
    AddressDto deliveryAddress;
}
