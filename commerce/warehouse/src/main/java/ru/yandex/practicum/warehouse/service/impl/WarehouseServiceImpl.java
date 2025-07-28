package ru.yandex.practicum.warehouse.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.warehouse.mapper.ProductInWarehouseMapper;
import ru.yandex.practicum.warehouse.model.Dimension;
import ru.yandex.practicum.warehouse.model.ProductInWarehouse;
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
    static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    final ProductInWarehouseRepository productInWarehouseRepository;

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
    public BookedProductDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCartDto) {
        log.info("Проверка наличия товаров на складе для корзины: {}", shoppingCartDto);

        Set<UUID> productIds = shoppingCartDto.getProducts().keySet();
        log.debug("Получение данных о {} товарах из хранилища", productIds.size());

        List<ProductInWarehouse> productsInWarehouse = productInWarehouseRepository.findAllById(productIds);

        Map<UUID, ProductInWarehouse> productMap = productsInWarehouse.stream()
                .collect(Collectors.toMap(ProductInWarehouse::getProductId, Function.identity()));

        double totalWeight = 0;
        double totalVolume = 0;

        for (Map.Entry<UUID, Integer> entry : shoppingCartDto.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();

            ProductInWarehouse product = productMap.get(productId);

            if (product == null || product.getQuantity() < quantity) {
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

        BookedProductDto bookedProductDto = new BookedProductDto();
        bookedProductDto.setDeliveryWeight(totalWeight);
        bookedProductDto.setDeliveryVolume(totalVolume);

        log.debug("Проверка корзины успешно завершена. Общий вес: {}, общий объем: {}", totalWeight, totalVolume);
        return bookedProductDto;
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
}
