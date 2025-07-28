package ru.yandex.practicum.warehouse.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.service.impl.WarehouseServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseController {
    final WarehouseServiceImpl warehouseService;

    @PutMapping
    public void newProductInWarehouse(@RequestBody NewProductInWarehouseRequest newProductInWarehouseRequest) {
        warehouseService.addNewProductToWarehouse(newProductInWarehouseRequest);
    }

    @PostMapping("/check")
    public BookedProductDto checkProductQuantityInWarehouse(@RequestBody ShoppingCartDto shoppingCartDto) {
        return warehouseService.checkProductQuantityInWarehouse(shoppingCartDto);
    }

    @PostMapping("/add")
    public void addProductToWarehouse(@RequestBody AddProductToWarehouseRequest addProductToWarehouseRequest) {
        warehouseService.addProductToWarehouse(addProductToWarehouseRequest);
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }
}

