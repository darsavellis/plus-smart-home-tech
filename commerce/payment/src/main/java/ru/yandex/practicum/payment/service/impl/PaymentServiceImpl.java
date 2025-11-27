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

import java.math.BigDecimal;
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
        BigDecimal totalPrice = orderDto.getTotalPrice();
        BigDecimal deliveryPrice = orderDto.getDeliveryPrice();
        Payment payment = Payment.builder()
            .paymentId(orderDto.getPaymentId())
            .totalPayment(totalPrice)
            .deliveryTotal(deliveryPrice)
            .feeTotal(calculateTax(totalPrice))
            .state(PaymentState.PENDING)
            .build();
        Payment savedPayment = paymentRepository.save(payment);
        log.debug("Создана и сохранена сущность платежа: {}", savedPayment);
        return PaymentMapper.toPaymentDto(savedPayment);
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        BigDecimal totalPrice = orderDto.getTotalPrice();
        BigDecimal deliveryPrice = orderDto.getDeliveryPrice();
        BigDecimal totalCost = calculateTax(totalPrice).add(deliveryPrice);
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
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        log.debug("Расчёт стоимости товаров для заказа orderId={}", orderDto.getOrderId());
        BigDecimal productsCost = BigDecimal.ZERO;
        Map<UUID, Integer> products = orderDto.getProducts();

        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            BigDecimal price = shoppingStoreClient.getProduct(entry.getKey()).getPrice();
            BigDecimal quantity = BigDecimal.valueOf(entry.getValue());
            BigDecimal cost = price.multiply(quantity);
            productsCost = productsCost.add(cost);
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

    private BigDecimal calculateTax(BigDecimal totalPrice) {
        BigDecimal result = totalPrice.add(totalPrice.multiply(BigDecimal.valueOf(0.1)));
        log.trace("Рассчитан НДС: исходная сумма={}, с налогом={}", totalPrice, result);
        return result;
    }
}
