package ru.yandex.practicum.order.service;

import org.springframework.data.domain.PageRequest;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.order.OrderDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderDto> getOrders(String username, PageRequest pageRequest);

    OrderDto createOrder(CreateNewOrderRequest createNewOrderRequest);

    OrderDto returnOrder(ProductReturnRequest productReturnRequest);

    OrderDto paymentOrder(UUID orderId);

    OrderDto paymentFailedOrder(UUID orderId);

    OrderDto deliveryOrder(UUID orderId);

    OrderDto deliveryFailed(UUID orderId);

    OrderDto completedOrder(UUID orderId);

    OrderDto calculateTotalOrder(UUID orderId);

    OrderDto calculateDeliveryOrder(UUID orderId);

    OrderDto assemblyOrder(UUID orderId);

    OrderDto assemblyFailedOrder(UUID orderId);
}
