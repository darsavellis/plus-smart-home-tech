package ru.yandex.practicum.payment.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "payments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id", columnDefinition = "uuid", updatable = false, nullable = false)
    UUID paymentId;

    @Column(name = "total_payment")
    double totalPayment;

    @Column(name = "delivery_total")
    double deliveryTotal;

    @Column(name = "fee_total")
    double feeTotal;

    @Enumerated(EnumType.STRING)
    PaymentState state;
}
