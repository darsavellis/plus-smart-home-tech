package ru.yandex.practicum.order.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contract.order.OrderContract;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.order.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderController implements OrderContract {
    OrderService orderService;

    @GetMapping
    public List<OrderDto> getOrders(String username, int from, int size) {
        return orderService.getOrders(username, PageRequest.of(from, size));
    }

    @PutMapping
    public OrderDto createOrder(CreateNewOrderRequest createNewOrderRequest) {
        return orderService.createOrder(createNewOrderRequest);
    }

    @PostMapping("/return")
    public OrderDto returnOrder(ProductReturnRequest productReturnRequest) {
        return orderService.returnOrder(productReturnRequest);
    }

    @PostMapping("/payment")
    public OrderDto paymentOrder(UUID orderId) {
        return orderService.paymentOrder(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto paymentFailedOrder(UUID orderId) {
        return orderService.paymentFailedOrder(orderId);
    }

    @PostMapping("/delivery")
    public OrderDto deliveryOrder(UUID orderId) {
        return orderService.deliveryOrder(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto deliveryFailedOrder(UUID orderId) {
        return orderService.paymentFailedOrder(orderId);
    }

    @PostMapping("/completed")
    public OrderDto completedOrder(UUID orderId) {
        return orderService.completedOrder(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotalOrder(UUID orderId) {
        return orderService.calculateTotalOrder(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryOrder(UUID orderId) {
        return orderService.calculateDeliveryOrder(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto assemblyOrder(UUID orderId) {
        return orderService.assemblyOrder(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailedOrder(UUID orderId) {
        return orderService.assemblyFailedOrder(orderId);
    }
}
