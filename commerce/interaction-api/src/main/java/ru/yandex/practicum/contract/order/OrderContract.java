package ru.yandex.practicum.contract.order;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderContract {
    @GetMapping
    List<OrderDto> getOrders(@RequestBody String username, @RequestParam(defaultValue = "0") int from,
                             @RequestParam(defaultValue = "10") int size);

    @PutMapping
    OrderDto createOrder(@Validated @RequestBody CreateNewOrderRequest createNewOrderRequest);

    @PostMapping("/return")
    OrderDto returnOrder(@Validated @RequestBody ProductReturnRequest productReturnRequest);

    @PostMapping("/payment")
    OrderDto paymentOrder(@RequestBody UUID orderId);

    @PostMapping("/payment/fail")
    OrderDto paymentFailedOrder(@RequestBody UUID orderId);

    @PostMapping("/delivery")
    OrderDto deliveryOrder(@RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailedOrder(@RequestBody UUID orderId);

    @PostMapping("/completed")
    OrderDto completedOrder(@RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalOrder(@RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryOrder(@RequestBody UUID orderId);

    @PostMapping("/assembly")
    OrderDto assemblyOrder(@RequestBody UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto assemblyFailedOrder(@RequestBody UUID orderId);
}
