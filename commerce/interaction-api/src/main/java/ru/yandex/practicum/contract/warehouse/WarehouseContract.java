package ru.yandex.practicum.contract.warehouse;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseContract {
    @PutMapping
    void addNewProductToWarehouse(@Validated @RequestBody NewProductInWarehouseRequest newProductInWarehouseRequest);

    @PostMapping("/check")
    BookedProductsDto checkProductQuantityInWarehouse(@Validated @RequestBody ShoppingCartDto shoppingCartDto);

    @PostMapping("/add")
    void addProductToWarehouse(@Validated @RequestBody AddProductToWarehouseRequest addProductToWarehouseRequest);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();

    @PostMapping("/assembly")
    BookedProductsDto assemblyProductsForOrder(@Validated @RequestBody AssemblyProductForOrderRequest assemblyProductForOrderRequest);

    @PostMapping("/shipped")
    void shippedToDelivery(@Valid @RequestBody ShippedToDeliveryRequest shippedToDeliveryRequest);

    @PostMapping("/return")
    void returnProducts(@RequestBody Map<UUID, Integer> products);
}
