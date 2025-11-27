package ru.yandex.practicum.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.contract.payment.PaymentContract;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient extends PaymentContract {
}
