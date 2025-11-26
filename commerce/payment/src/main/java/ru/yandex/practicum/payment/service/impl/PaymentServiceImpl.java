package ru.yandex.practicum.payment.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.payment.client.OrderClient;
import ru.yandex.practicum.payment.client.ShoppingStoreClient;
import ru.yandex.practicum.payment.exception.NoOrderFoundException;
import ru.yandex.practicum.payment.mapper.PaymentMapper;
import ru.yandex.practicum.payment.model.Payment;
import ru.yandex.practicum.payment.model.PaymentState;
import ru.yandex.practicum.payment.repository.PaymentRepository;
import ru.yandex.practicum.payment.service.PaymentService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentServiceImpl implements PaymentService {
    final PaymentRepository paymentRepository;
    final ShoppingStoreClient shoppingStoreClient;
    final OrderClient orderClient;

    @Override
    public PaymentDto createPayment(OrderDto orderDto) {
        log.info("Создание платежа для заказа orderId={}, сумма={}, доставка={}",
            orderDto.getOrderId(), orderDto.getTotalPrice(), orderDto.getDeliveryPrice());
        Payment payment = Payment.builder()
            .paymentId(orderDto.getPaymentId())
            .totalPayment(orderDto.getTotalPrice())
            .deliveryTotal(orderDto.getDeliveryPrice())
            .feeTotal(calculateTax(orderDto.getTotalPrice()))
            .state(PaymentState.PENDING)
            .build();
        Payment savedPayment = paymentRepository.save(payment);
        log.debug("Создана и сохранена сущность платежа: {}", savedPayment);
        return PaymentMapper.toPaymentDto(savedPayment);
    }

    @Override
    public double calculateTotalCost(OrderDto orderDto) {
        double totalCost = calculateTax(orderDto.getTotalPrice()) + orderDto.getDeliveryPrice();
        log.debug("Рассчитана итоговая стоимость заказа orderId={}: {}", orderDto.getOrderId(), totalCost);
        return totalCost;
    }

    @Override
    public void refundPayment(UUID orderId) {
        log.info("Успешная оплата платежа для заказа orderId={}", orderId);
        Payment payment = paymentRepository.findById(orderId)
            .orElseThrow(() -> new NoOrderFoundException("Платёж для заказа с id=" + orderId + " не найден"));
        payment.setState(PaymentState.SUCCESS);
        paymentRepository.save(payment);
        log.debug("Статус платежа для заказа orderId={} изменён на SUCCESS", orderId);
        orderClient.paymentOrder(orderId);
        log.info("Оплата для заказа orderId={} успешно выполнена", orderId);
    }

    @Override
    public double calculateProductCost(OrderDto orderDto) {
        log.debug("Расчёт стоимости товаров для заказа orderId={}", orderDto.getOrderId());
        double productsCost = 0;
        Map<UUID, Integer> products = orderDto.getProducts();

        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            double price = shoppingStoreClient.getProduct(entry.getKey()).getPrice();
            double quantity = entry.getValue();
            double cost = price * quantity;
            productsCost += cost;
            log.trace("Товар productId={}, цена={}, количество={}, стоимость={}",
                entry.getKey(), price, quantity, cost);
        }

        log.debug("Суммарная стоимость товаров для заказа orderId={} равна {}", orderDto.getOrderId(), productsCost);
        return productsCost;
    }

    @Override
    public void failedPayment(UUID orderId) {
        log.warn("Помечаем платёж как FAILED для заказа orderId={}", orderId);
        Payment payment = paymentRepository.findById(orderId)
            .orElseThrow(() -> new NoOrderFoundException("Платёж для заказа с id=" + orderId + " не найден"));
        payment.setState(PaymentState.FAILED);
        orderClient.paymentOrder(orderId);
        log.info("Платёж для заказа orderId={} помечен как FAILED", orderId);
    }

    double calculateTax(double totalPrice) {
        double result = totalPrice + totalPrice * 0.1;
        log.trace("Рассчитан НДС: исходная сумма={}, с налогом={}", totalPrice, result);
        return result;
    }
}
