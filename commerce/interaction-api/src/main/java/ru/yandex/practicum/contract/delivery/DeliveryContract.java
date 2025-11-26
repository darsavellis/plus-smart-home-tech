package ru.yandex.practicum.contract.delivery;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryContract {
    @PutMapping
    DeliveryDto createDelivery(@Valid @RequestBody DeliveryDto deliveryDto);

    @PostMapping("/successful")
    void deliverySuccessful(UUID orderId);

    @PostMapping("/picked")
    void deliveryPicked(UUID orderID);

    @PostMapping("/failed")
    void deliveryFailed(UUID orderId);

    @PostMapping("/cost")
    BigDecimal calculateDeliveryCost(OrderDto orderDto);
}
