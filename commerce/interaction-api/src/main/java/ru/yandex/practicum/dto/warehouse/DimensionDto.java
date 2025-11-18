package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {
    @Min(value = 1)
    Double width;
    @Min(value = 1)
    Double height;
    @Min(value = 1)
    Double depth;
}
