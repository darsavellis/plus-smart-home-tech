package ru.yandex.practicum.contract.warehouse;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;

public interface WarehouseContract {
    void addNewProductToWarehouse(@RequestBody NewProductInWarehouseRequest newProductInWarehouseRequest);

    BookedProductDto checkProductQuantityInWarehouse(@RequestBody ShoppingCartDto shoppingCartDto);

    void addProductToWarehouse(@RequestBody AddProductToWarehouseRequest addProductToWarehouseRequest);

    AddressDto getWarehouseAddress();
}
