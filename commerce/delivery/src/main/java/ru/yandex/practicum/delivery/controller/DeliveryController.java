package ru.yandex.practicum.delivery.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.contract.delivery.DeliveryContract;
import ru.yandex.practicum.delivery.service.DeliveryService;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryController implements DeliveryContract {
    DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        return deliveryService.createDelivery(deliveryDto);
    }

    @PostMapping("/successful")
    public void deliverySuccessful(UUID orderId) {
        deliveryService.deliverySuccessful(orderId);
    }

    @PostMapping("/picked")
    public void deliveryPicked(UUID orderID) {
        deliveryService.deliveryPicked(orderID);
    }

    @PostMapping("/failed")
    public void deliveryFailed(UUID orderId) {
        deliveryService.deliveryFailed(orderId);
    }

    @PostMapping("/cost")
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        return deliveryService.calculateDeliveryCost(orderDto);
    }
}
