package ru.yandex.practicum.dto.warehouse;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssemblyProductForOrderRequest {
    Map<UUID, Long> products;
    UUID orderId;
}
