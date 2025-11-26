package ru.yandex.practicum.payment.mapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.payment.model.Payment;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentMapper {
    public Payment toPayment(PaymentDto paymentDto) {
        return Payment.builder()
            .paymentId(paymentDto.getPaymentId())
            .totalPayment(paymentDto.getTotalPayment())
            .deliveryTotal(paymentDto.getDeliveryTotal())
            .feeTotal(paymentDto.getFeeTotal())
            .build();
    }

    public PaymentDto toPaymentDto(Payment payment) {
        return PaymentDto.builder()
            .paymentId(payment.getPaymentId())
            .totalPayment(payment.getTotalPayment())
            .deliveryTotal(payment.getDeliveryTotal())
            .feeTotal(payment.getFeeTotal())
            .build();
    }
}
