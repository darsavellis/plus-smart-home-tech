package ru.yandex.practicum.order.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.OrderState;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.order.client.DeliveryClient;
import ru.yandex.practicum.order.client.PaymentClient;
import ru.yandex.practicum.order.exceptions.NoOrderFoundException;
import ru.yandex.practicum.order.mapper.OrderMapper;
import ru.yandex.practicum.order.model.Order;
import ru.yandex.practicum.order.repository.OrderRepository;
import ru.yandex.practicum.order.service.OrderService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderServiceImpl implements OrderService {
    final OrderRepository orderRepository;
    final DeliveryClient deliveryClient;
    final PaymentClient paymentClient;

    @Override
    public List<OrderDto> getOrders(String username, PageRequest pageRequest) {
        return orderRepository.findAllByUsername(username, pageRequest).map(OrderMapper::toOrderDto).getContent();
    }

    @Override
    public OrderDto createOrder(CreateNewOrderRequest createNewOrderRequest) {
        Order order = Order.builder()
            .shoppingCartId(createNewOrderRequest.getShoppingCart().getShoppingCartId())
            .products(createNewOrderRequest.getShoppingCart().getProducts())
            .state(OrderState.NEW)
            .build();

        return OrderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest productReturnRequest) {
        return null;
    }

    @Override
    public OrderDto paymentOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NoOrderFoundException("Error message"));
        order.setState(OrderState.ON_PAYMENT);
        return OrderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto paymentFailedOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NoOrderFoundException("Error message"));
        order.setState(OrderState.PAYMENT_FAILED);
        return OrderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto deliveryOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NoOrderFoundException("Error message"));
        order.setState(OrderState.ON_DELIVERY);
        return OrderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NoOrderFoundException("Error message"));
        order.setState(OrderState.DELIVERY_FAILED);
        return OrderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto completedOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NoOrderFoundException("Error message"));
        order.setState(OrderState.COMPLETED);
        return OrderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto calculateTotalOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NoOrderFoundException("Error message"));
        OrderDto orderDto = OrderMapper.toOrderDto(order);
        order.setDeliveryPrice(paymentClient.calculateTotalCost(orderDto));
        return orderDto;
    }

    @Override
    public OrderDto calculateDeliveryOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NoOrderFoundException("Error message"));
        OrderDto orderDto = OrderMapper.toOrderDto(order);
        order.setDeliveryPrice(deliveryClient.calculateDeliveryCost(orderDto));
        return orderDto;
    }

    @Override
    public OrderDto assemblyOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NoOrderFoundException("Error message"));
        order.setState(OrderState.ASSEMBLED);
        return OrderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto assemblyFailedOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NoOrderFoundException("Error message"));
        order.setState(OrderState.ASSEMBLY_FAILED);
        return OrderMapper.toOrderDto(order);
    }
}
