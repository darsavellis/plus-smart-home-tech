package ru.yandex.practicum.delivery.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.client.OrderClient;
import ru.yandex.practicum.delivery.client.WarehouseClient;
import ru.yandex.practicum.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.delivery.service.DeliveryService;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    final DeliveryRepository deliveryRepository;
    final OrderClient orderClient;
    final WarehouseClient warehouseClient;

    static final double BASERATE = 5;
    static final double FRAGILE_SURCHARGE_RATE = 0.2;
    static final double WEIGHT_RATE = 0.3;
    static final double VOLUME_RATE = 0.2;
    static final double STREET_SURCHARGE_RATE = 0.2;
    static final String ADDRESS1 = "ADDRESS_1";
    static final String ADDRESS2 = "ADDRESS_2";
    static final String MESSAGE_DELIVERY_NOT_FOUND = "Доставка не найдена.";

    @Override
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = DeliveryMapper.toDelivery(deliveryDto);
        delivery.setDeliveryState(DeliveryState.CREATED);
        return DeliveryMapper.toDeliveryDto(deliveryRepository.save(delivery));
    }

    private Delivery findDeliveryById(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId).orElseThrow(
            () -> new NoDeliveryFoundException(MESSAGE_DELIVERY_NOT_FOUND));
    }

    @Override
    public void deliverySuccessful(UUID deliveryId) {
        Delivery delivery = findDeliveryById(deliveryId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        log.info("Доставка {} отмечена как DELIVERED", deliveryId);
    }

    @Override
    public void deliveryPicked(UUID deliveryId) {
        Delivery delivery = findDeliveryById(deliveryId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        log.info("Доставка {} отмечена как IN_PROGRESS", deliveryId);
    }

    @Override
    public void deliveryFailed(UUID deliveryId) {
        Delivery delivery = findDeliveryById(deliveryId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        orderClient.assemblyFailedOrder(delivery.getOrderId());
        log.info("Доставка {} отмечена как FAILED", deliveryId);
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateDeliveryCost(OrderDto orderDto) {
        if (orderDto == null) {
            throw new IllegalArgumentException("Order DTO не может быть null");
        }

        Delivery delivery = deliveryRepository.findByOrderId(orderDto.getOrderId()).orElseThrow(
            () -> new NoDeliveryFoundException(MESSAGE_DELIVERY_NOT_FOUND));

        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();
        validateWarehouseAddress(warehouseAddress);

        double baseAddressCost = calculateAddressCost(warehouseAddress.getCity());
        double baseCost = BASERATE + baseAddressCost;

        double fragileCharge = calculateFragileCharge(orderDto, baseCost);
        double weightCharge = calculateWeightCharge(orderDto);
        double volumeCharge = calculateVolumeCharge(orderDto);
        double streetCharge = calculateStreetCharge(warehouseAddress, delivery, baseCost, fragileCharge);

        double totalDeliveryCost = baseCost + fragileCharge + weightCharge + volumeCharge + streetCharge;
        log.debug("Рассчитана стоимость доставки для заказа {}: {}", orderDto.getOrderId(), totalDeliveryCost);

        return totalDeliveryCost;
    }

    private void validateWarehouseAddress(AddressDto warehouseAddress) {
        if (warehouseAddress == null) {
            throw new IllegalStateException("Адрес склада не может быть null");
        }
        if (warehouseAddress.getCity() == null) {
            throw new IllegalStateException("Город склада не может быть null");
        }
    }

    private double calculateFragileCharge(OrderDto orderDto, double baseCost) {
        return orderDto.isFragile() ? baseCost * FRAGILE_SURCHARGE_RATE : 0;
    }

    private double calculateWeightCharge(OrderDto orderDto) {
        return orderDto.getDeliveryWeight() * WEIGHT_RATE;
    }

    private double calculateVolumeCharge(OrderDto orderDto) {
        return orderDto.getDeliveryVolume() * VOLUME_RATE;
    }

    private double calculateStreetCharge(AddressDto warehouseAddress,
                                         Delivery delivery,
                                         double baseCost,
                                         double fragileCharge) {
        return isStreetSurchargeApplicable(warehouseAddress, delivery)
            ? (baseCost + fragileCharge) * STREET_SURCHARGE_RATE
            : 0;
    }

    private double calculateAddressCost(String city) {
        return switch (city) {
            case ADDRESS1 -> BASERATE;
            case ADDRESS2 -> BASERATE * 2.0;
            default -> throw new IllegalStateException(String.format("Unexpected city value: %s", city));
        };
    }

    private boolean isStreetSurchargeApplicable(AddressDto warehouseAddress, Delivery delivery) {
        return warehouseAddress.getStreet() != null &&
               delivery.getToAddress() != null &&
               !warehouseAddress.getStreet().equals(delivery.getToAddress().getStreet());
    }
}
