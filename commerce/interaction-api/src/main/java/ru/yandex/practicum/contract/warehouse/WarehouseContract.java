package ru.yandex.practicum.contract.warehouse;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;

public interface WarehouseContract {
    void addNewProductToWarehouse(@Validated @RequestBody NewProductInWarehouseRequest newProductInWarehouseRequest);

    BookedProductDto checkProductQuantityInWarehouse(@Validated @RequestBody ShoppingCartDto shoppingCartDto);

    void addProductToWarehouse(@Validated @RequestBody AddProductToWarehouseRequest addProductToWarehouseRequest);

    AddressDto getWarehouseAddress();
}
