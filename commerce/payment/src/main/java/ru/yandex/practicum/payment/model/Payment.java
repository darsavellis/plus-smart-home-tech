package ru.yandex.practicum.payment.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id", columnDefinition = "uuid", updatable = false, nullable = false)
    UUID paymentId;

    @Column(name = "total_payment", precision = 19, scale = 2)
    BigDecimal totalPayment;

    @Column(name = "delivery_total", precision = 19, scale = 2)
    BigDecimal deliveryTotal;

    @Column(name = "fee_total", precision = 19, scale = 2)
    BigDecimal feeTotal;

    @Enumerated(EnumType.STRING)
    PaymentState state;
}
