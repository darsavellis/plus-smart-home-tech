package ru.yandex.practicum.warehouse.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.warehouse.mapper.ProductInWarehouseMapper;
import ru.yandex.practicum.warehouse.model.Dimension;
import ru.yandex.practicum.warehouse.model.OrderBooking;
import ru.yandex.practicum.warehouse.model.ProductInWarehouse;
import ru.yandex.practicum.warehouse.repository.OrderBookingRepository;
import ru.yandex.practicum.warehouse.repository.ProductInWarehouseRepository;
import ru.yandex.practicum.warehouse.service.WarehouseService;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseServiceImpl implements WarehouseService {
    static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};
    static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    final ProductInWarehouseRepository productInWarehouseRepository;
    final OrderBookingRepository orderBookingRepository;

    @Override
    @Transactional
    public void addNewProductToWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest) {
        log.info("Начало добавления нового продукта в хранилище. ID продукта: {}", newProductInWarehouseRequest.getProductId());
        if (productInWarehouseRepository.existsById(newProductInWarehouseRequest.getProductId())) {
            log.warn("Продукт с ID {} уже существует в хранилище", newProductInWarehouseRequest.getProductId());
            throw new SpecifiedProductAlreadyInWarehouseException(
                    String.format("Продукт с ID %s уже существует в хранилище", newProductInWarehouseRequest.getProductId()));
        }

        ProductInWarehouse productInWarehouse = ProductInWarehouseMapper.toProductInWarehouse(newProductInWarehouseRequest);
        productInWarehouse.setProductId(newProductInWarehouseRequest.getProductId());
        log.debug("Сохранение продукта: {}", productInWarehouse);
        productInWarehouseRepository.save(productInWarehouse);
        log.info("Продукт успешно добавлен в хранилище. ID продукта: {}", newProductInWarehouseRequest.getProductId());
    }

    @Override
    public BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCartDto) {
        log.info("Проверка наличия товаров на складе для корзины: {}", shoppingCartDto);

        Map<UUID, Integer> cartProducts = shoppingCartDto.getProducts();
        if (cartProducts == null || cartProducts.isEmpty()) {
            log.warn("Корзина пуста, товаров для проверки нет");
            return new BookedProductsDto();
        }

        Set<UUID> productIds = cartProducts.keySet();
        log.debug("Получение данных о {} товарах из хранилища", productIds.size());

        Map<UUID, ProductInWarehouse> productMap = productInWarehouseRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(ProductInWarehouse::getProductId, Function.identity()));

        double totalWeight = 0;
        double totalVolume = 0;

        for (Map.Entry<UUID, Integer> entry : cartProducts.entrySet()) {
            UUID productId = entry.getKey();
            int quantity = entry.getValue();

            if (quantity <= 0) {
                log.debug("Пропуск товара {} с неположительным количеством: {}", productId, quantity);
                continue;
            }

            ProductInWarehouse product = productMap.get(productId);

            if (product == null || (product.getQuantity() == null ? 0L : product.getQuantity()) < quantity) {
                log.warn("Товар {} отсутствует или имеет недостаточное количество на складе", productId);
                throw new ProductInShoppingCartLowQuantityWarehouse(
                        String.format("Товар с ID %s отсутствует или имеет недостаточное количество на складе", productId));
            }

            if (product.getWeight() != null) {
                totalWeight += product.getWeight() * quantity;
            }

            Dimension dimension = product.getDimension();
            if (dimension != null) {
                totalVolume += dimension.getWidth() * dimension.getHeight() * dimension.getDepth() * quantity;
            }
        }

        BookedProductsDto bookedProductsDto = new BookedProductsDto();
        bookedProductsDto.setDeliveryWeight(totalWeight);
        bookedProductsDto.setDeliveryVolume(totalVolume);

        log.debug("Проверка корзины успешно завершена. Общий вес: {}, общий объем: {}", totalWeight, totalVolume);
        return bookedProductsDto;
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequest) {
        log.info("Запрос на добавление продукта на склад. ID продукта: {}, количество: {}",
                addProductToWarehouseRequest.getProductId(), addProductToWarehouseRequest.getQuantity());

        ProductInWarehouse product = productInWarehouseRepository.findById(addProductToWarehouseRequest.getProductId())
                .orElseThrow(() -> {
                    log.error("Продукт с ID {} не найден в хранилище", addProductToWarehouseRequest.getProductId());
                    return new SpecifiedProductAlreadyInWarehouseException(
                            String.format("Продукт с ID %s не найден в хранилище", addProductToWarehouseRequest.getProductId()));
                });

        long currentQuantity = (product.getQuantity() != null) ? product.getQuantity() : 0L;
        log.debug("Текущее количество продукта: {}", currentQuantity);

        long newQuantity = currentQuantity + addProductToWarehouseRequest.getQuantity();
        product.setQuantity(newQuantity);

        productInWarehouseRepository.save(product);
        log.info("Количество продукта с ID {} успешно обновлено. Новое количество: {}",
                addProductToWarehouseRequest.getProductId(), newQuantity);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("Получение адреса склада");
        AddressDto addressDto = AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
        log.debug("Сформирован адрес склада: {}", addressDto);
        return addressDto;
    }


    @Override
    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductForOrderRequest request) {
        log.info("Сборка товаров для заказа. shoppingCartId: {}", request.getOrderId());
        Map<UUID, Long> products = request.getProducts();
        List<ProductInWarehouse> items = productInWarehouseRepository.findAllById(products.keySet());
        Map<UUID, ProductInWarehouse> itemById = items.stream()
                .collect(Collectors.toMap(ProductInWarehouse::getProductId, Function.identity()));

        for (Map.Entry<UUID, Long> e : products.entrySet()) {
            UUID productId = e.getKey();
            long need = e.getValue();
            ProductInWarehouse pw = itemById.get(productId);
            if (pw == null || (pw.getQuantity() == null ? 0L : pw.getQuantity()) < need) {
                throw new ProductInShoppingCartLowQuantityWarehouse(
                        String.format("Товар %s отсутствует или недостаточен на складе", productId));
            }
        }
        for (Map.Entry<UUID, Long> e : products.entrySet()) {
            ProductInWarehouse pw = itemById.get(e.getKey());
            long current = pw.getQuantity() == null ? 0L : pw.getQuantity();
            pw.setQuantity(current - e.getValue());
        }
        productInWarehouseRepository.saveAll(items);

        double deliveryWeight = 0.0;
        double deliveryVolume = 0.0;
        boolean fragile = false;
        for (ProductInWarehouse pw : items) {
            long qty = products.getOrDefault(pw.getProductId(), 0L);
            if (qty <= 0) continue;
            if (pw.getWeight() != null) {
                deliveryWeight += pw.getWeight() * qty;
            }
            Dimension d = pw.getDimension();
            if (d != null) {
                deliveryVolume += d.getWidth() * d.getHeight() * d.getDepth() * qty;
            }
            fragile = fragile || Boolean.TRUE.equals(pw.getFragile());
        }

        OrderBooking booking = OrderBooking.builder()
                .orderId(request.getOrderId())
                .products(products)
                .deliveryWeight(deliveryWeight)
                .deliveryVolume(deliveryVolume)
                .fragile(fragile)
                .build();
        orderBookingRepository.save(booking);

        BookedProductsDto dto = new BookedProductsDto();
        dto.setDeliveryWeight(deliveryWeight);
        dto.setDeliveryVolume(deliveryVolume);
        dto.setFragile(fragile);
        return dto;
    }

    @Override
    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        log.info("Отгрузка на доставку. orderId: {}, deliveryId: {}", request.getOrderId(), request.getDeliveryId());
        OrderBooking booking = orderBookingRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Сборка для заказа %s не найдена", request.getOrderId())));
        booking.setDeliveryId(request.getDeliveryId());
        orderBookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void returnProducts(Map<UUID, Integer> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        List<ProductInWarehouse> warehousesItems = productInWarehouseRepository.findAllById(products.keySet());
        for (ProductInWarehouse warehouseItem : warehousesItems) {
            long current = warehouseItem.getQuantity() == null ? 0L : warehouseItem.getQuantity();
            long inc = products.getOrDefault(warehouseItem.getProductId(), 0);
            warehouseItem.setQuantity(current + inc);
        }
        productInWarehouseRepository.saveAll(warehousesItems);
    }
}
