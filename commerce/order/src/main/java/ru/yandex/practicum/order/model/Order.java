package ru.yandex.practicum.order.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.order.OrderState;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "orders")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", columnDefinition = "uuid", updatable = false, nullable = false)
    UUID orderId;

    @Column(name = "username")
    String username;

    @Column(name = "shopping_cart_id")
    UUID shoppingCartId;

    @ElementCollection
    @CollectionTable(name = "order_products",
        joinColumns = {@JoinColumn(name = "order_id", referencedColumnName = "order_id")})
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    Map<UUID, Integer> products;

    @Column(name = "payment_id")
    UUID paymentId;

    @Column(name = "delivery_id")
    UUID deliveryId;

    @Enumerated(EnumType.STRING)
    OrderState state;

    @Column(name = "delivery_weight")
    double deliveryWeight;

    @Column(name = "delivery_volume")
    double deliveryVolume;

    @Column(name = "fragile")
    boolean fragile;

    @Column(name = "total_price")
    double totalPrice;

    @Column(name = "delivery_price")
    double deliveryPrice;

    @Column(name = "product_price")
    double productPrice;
}

