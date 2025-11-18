package ru.yandex.practicum.cart.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.contract.warehouse.WarehouseContract;

@FeignClient(name = "warehouse")
public interface WarehouseClient extends WarehouseContract {
}
