package ru.yandex.practicum.payment.service;

import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.util.UUID;

public interface PaymentService {
    PaymentDto createPayment(OrderDto orderDto);

    double calculateTotalCost(OrderDto orderDto);

    void refundPayment(UUID orderId);

    double calculateProductCost(OrderDto orderDto);

    void failedPayment(UUID orderId);
}
