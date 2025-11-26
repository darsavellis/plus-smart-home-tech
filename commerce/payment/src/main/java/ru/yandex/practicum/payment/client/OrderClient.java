package ru.yandex.practicum.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.contract.order.OrderContract;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient extends OrderContract {
}
