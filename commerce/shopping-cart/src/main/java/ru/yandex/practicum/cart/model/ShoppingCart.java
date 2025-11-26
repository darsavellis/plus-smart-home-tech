package ru.yandex.practicum.cart.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.dto.cart.CartState;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "shopping_carts")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "shopping_cart_id", columnDefinition = "uuid")
    UUID shoppingCartId;

    @Column(name = "user_id")
    String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "shopping_cart_state")
    CartState cartState;

    @ElementCollection
    @CollectionTable(name = "shopping_cart_product_quantity",
        joinColumns = {@JoinColumn(name = "shopping_cart_id", referencedColumnName = "shopping_cart_id")})
    @MapKeyColumn(name = "product_name")
    @Column(name = "quantity")
    Map<UUID, Integer> productQuantityMap;
}
