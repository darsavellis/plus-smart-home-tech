package ru.yandex.practicum.delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.contract.warehouse.WarehouseContract;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient extends WarehouseContract {
}
