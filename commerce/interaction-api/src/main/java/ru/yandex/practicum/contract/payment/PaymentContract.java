package ru.yandex.practicum.contract.payment;


import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.util.UUID;

public interface PaymentContract {
    @PostMapping
    PaymentDto createPayment(@Validated @RequestBody OrderDto orderDto);

    @PostMapping("/totalCost")
    double calculateTotalCost(@Validated @RequestBody OrderDto orderDto);

    @PostMapping("/refund")
    void refundPayment(@Validated @RequestBody UUID orderId);

    @PostMapping("/productCost")
    double calculateProductCost(@Validated @RequestBody OrderDto orderDto);

    @PostMapping("/failed")
    void failedPayment(@Validated @RequestBody UUID orderId);
}
