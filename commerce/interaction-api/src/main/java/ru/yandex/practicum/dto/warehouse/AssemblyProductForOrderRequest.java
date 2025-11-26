package ru.yandex.practicum.dto.warehouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssemblyProductForOrderRequest {
    Map<UUID, Long> products;
    UUID orderId;
}
