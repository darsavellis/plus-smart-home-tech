package ru.yandex.practicum.warehouse.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dimension {
    Double width;
    Double height;
    Double depth;
}
