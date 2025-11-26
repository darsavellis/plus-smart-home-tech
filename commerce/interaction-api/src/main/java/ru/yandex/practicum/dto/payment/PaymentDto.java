package ru.yandex.practicum.dto.payment;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentDto {
    UUID paymentId;
    BigDecimal totalPayment;
    BigDecimal deliveryTotal;
    BigDecimal feeTotal;
}
