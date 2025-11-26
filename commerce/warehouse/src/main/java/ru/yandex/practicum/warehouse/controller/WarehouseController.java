package ru.yandex.practicum.warehouse.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contract.warehouse.WarehouseContract;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.warehouse.service.impl.WarehouseServiceImpl;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseController implements WarehouseContract {
    final WarehouseServiceImpl warehouseService;

    @PutMapping
    public void addNewProductToWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest) {
        warehouseService.addNewProductToWarehouse(newProductInWarehouseRequest);
    }

    @PostMapping("/check")
    public BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCartDto) {
        return warehouseService.checkProductQuantityInWarehouse(shoppingCartDto);
    }

    @PostMapping("/add")
    public void addProductToWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequest) {
        warehouseService.addProductToWarehouse(addProductToWarehouseRequest);
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductForOrderRequest assemblyProductForOrderRequest) {
        return warehouseService.assemblyProductsForOrder(assemblyProductForOrderRequest);
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest shippedToDeliveryRequest) {
        warehouseService.shippedToDelivery(shippedToDeliveryRequest);
    }

    @Override
    public void returnProducts(Map<UUID, Integer> products) {
        warehouseService.returnProducts(products);
    }
}

