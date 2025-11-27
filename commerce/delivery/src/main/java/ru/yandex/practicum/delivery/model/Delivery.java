package ru.yandex.practicum.delivery.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.dto.delivery.DeliveryState;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID deliveryId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "from_address",
        joinColumns = @JoinColumn(name = "delivery_id"),
        inverseJoinColumns = @JoinColumn(name = "address_id"))
    private Address fromAddress;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "to_address",
        joinColumns = @JoinColumn(name = "delivery_id"),
        inverseJoinColumns = @JoinColumn(name = "address_id"))
    private Address toAddress;

    @Column(name = "order_id")
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    private DeliveryState deliveryState;
}
