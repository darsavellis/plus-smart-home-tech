package ru.yandex.practicum.warehouse.service;

import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;

public interface WarehouseService {
    void addNewProductToWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest);

    BookedProductDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCartDto);

    void addProductToWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequest);

    AddressDto getWarehouseAddress();
}
