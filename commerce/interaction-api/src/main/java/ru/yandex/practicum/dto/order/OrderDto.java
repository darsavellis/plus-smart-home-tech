package ru.yandex.practicum.dto.order;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto {
    UUID orderId;
    UUID shoppingCartId;
    Map<UUID, Integer> products;
    UUID paymentId;
    UUID deliveryId;
    OrderState state;
    double deliveryWeight;
    double deliveryVolume;
    boolean fragile;
    double totalPrice;
    double deliveryPrice;
    double productPrice;
}
