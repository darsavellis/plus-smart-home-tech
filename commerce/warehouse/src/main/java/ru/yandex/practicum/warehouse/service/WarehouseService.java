package ru.yandex.practicum.warehouse.service;

import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void addNewProductToWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest);

    BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCartDto);

    void addProductToWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequest);

    AddressDto getWarehouseAddress();

    BookedProductsDto assemblyProductsForOrder(AssemblyProductForOrderRequest assemblyProductsForOrderRequest);

    void shippedToDelivery(ShippedToDeliveryRequest shippedToDeliveryRequest);

    void returnProducts(Map<UUID, Integer> products);
}
