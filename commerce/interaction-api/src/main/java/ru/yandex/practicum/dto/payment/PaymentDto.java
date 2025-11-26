package ru.yandex.practicum.dto.payment;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentDto {
    UUID paymentId;
    double totalPayment;
    double deliveryTotal;
    double feeTotal;
}
