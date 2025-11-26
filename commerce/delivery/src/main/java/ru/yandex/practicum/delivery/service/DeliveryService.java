package ru.yandex.practicum.delivery.service;

import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.util.UUID;

public interface DeliveryService {
    DeliveryDto createDelivery(DeliveryDto deliveryDto);

    void deliverySuccessful(UUID orderId);

    void deliveryPicked(UUID orderId);

    void deliveryFailed(UUID orderId);

    double calculateDeliveryCost(OrderDto orderDto);
}
