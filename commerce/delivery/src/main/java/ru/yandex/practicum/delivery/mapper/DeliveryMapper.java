package ru.yandex.practicum.delivery.mapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.dto.delivery.DeliveryDto;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryMapper {
    public Delivery toDelivery(DeliveryDto deliveryDto) {
        return Delivery.builder()
            .deliveryId(deliveryDto.getDeliveryId())
            .fromAddress(AddressMapper.toAddress(deliveryDto.getFromAddress()))
            .toAddress(AddressMapper.toAddress(deliveryDto.getToAddress()))
            .orderId(deliveryDto.getOrderId())
            .deliveryState(deliveryDto.getDeliveryState())
            .build();
    }

    public DeliveryDto toDeliveryDto(Delivery delivery) {
        return DeliveryDto.builder()
            .deliveryId(delivery.getDeliveryId())
            .fromAddress(AddressMapper.toAddressDto(delivery.getFromAddress()))
            .toAddress(AddressMapper.toAddressDto(delivery.getToAddress()))
            .orderId(delivery.getOrderId())
            .deliveryState(delivery.getDeliveryState())
            .build();
    }
}
