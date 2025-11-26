package ru.yandex.practicum.payment.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.contract.payment.PaymentContract;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentController implements PaymentContract {
    PaymentService paymentService;

    @PostMapping
    public PaymentDto createPayment(OrderDto orderDto) {
        return paymentService.createPayment(orderDto);
    }

    @PostMapping("/totalCost")
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        return paymentService.calculateTotalCost(orderDto);
    }

    @PostMapping("/refund")
    public void refundPayment(UUID orderId) {
        paymentService.refundPayment(orderId);
    }

    @PostMapping("/productCost")
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        return paymentService.calculateProductCost(orderDto);
    }

    @PostMapping("/failed")
    public void failedPayment(UUID orderId) {
        paymentService.failedPayment(orderId);
    }
}
