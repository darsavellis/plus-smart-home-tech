package ru.yandex.practicum.dto.warehouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShippedToDeliveryRequest {
    UUID orderId;
    UUID deliveryId;
}
