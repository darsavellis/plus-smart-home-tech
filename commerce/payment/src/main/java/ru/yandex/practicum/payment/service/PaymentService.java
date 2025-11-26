package ru.yandex.practicum.payment.service;

import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentDto createPayment(OrderDto orderDto);

    BigDecimal calculateTotalCost(OrderDto orderDto);

    void refundPayment(UUID orderId);

    BigDecimal calculateProductCost(OrderDto orderDto);

    void failedPayment(UUID orderId);
}
