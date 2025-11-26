package ru.yandex.practicum.dto.delivery;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.warehouse.AddressDto;

import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryDto {
    UUID deliveryId;
    AddressDto fromAddress;
    AddressDto toAddress;
    UUID orderId;
    DeliveryState deliveryState;
}
