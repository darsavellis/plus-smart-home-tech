package ru.yandex.practicum.warehouse.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.warehouse.DimensionDto;
import ru.yandex.practicum.warehouse.model.Dimension;

@UtilityClass
public class DimensionMapper {
    Dimension mapToDimension(DimensionDto dimensionDto) {
        Dimension dimension = new Dimension();
        dimension.setWidth(dimensionDto.getWidth());
        dimension.setHeight(dimensionDto.getHeight());
        dimension.setDepth(dimensionDto.getDepth());
        return dimension;
    }
}
