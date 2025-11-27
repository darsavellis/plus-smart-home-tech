package ru.yandex.practicum.order.mapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.order.model.Order;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderMapper {
    public Order toOrder(OrderDto orderDto) {
        return Order.builder()
            .orderId(orderDto.getOrderId())
            .shoppingCartId(orderDto.getShoppingCartId())
            .products(orderDto.getProducts())
            .paymentId(orderDto.getPaymentId())
            .deliveryId(orderDto.getDeliveryId())
            .state(orderDto.getState())
            .deliveryWeight(orderDto.getDeliveryWeight())
            .deliveryVolume(orderDto.getDeliveryVolume())
            .fragile(orderDto.isFragile())
            .totalPrice(orderDto.getTotalPrice())
            .deliveryPrice(orderDto.getDeliveryPrice())
            .productPrice(orderDto.getProductPrice())
            .build();
    }

    public OrderDto toOrderDto(Order order) {
        return OrderDto.builder()
            .orderId(order.getOrderId())
            .shoppingCartId(order.getShoppingCartId())
            .products(order.getProducts())
            .paymentId(order.getPaymentId())
            .deliveryId(order.getDeliveryId())
            .state(order.getState())
            .deliveryWeight(order.getDeliveryWeight())
            .deliveryVolume(order.getDeliveryVolume())
            .fragile(order.isFragile())
            .totalPrice(order.getTotalPrice())
            .deliveryPrice(order.getDeliveryPrice())
            .productPrice(order.getProductPrice())
            .build();
    }
}
