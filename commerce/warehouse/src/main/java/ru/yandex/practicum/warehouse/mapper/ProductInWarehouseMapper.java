package ru.yandex.practicum.warehouse.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.model.ProductInWarehouse;

@UtilityClass
public class ProductInWarehouseMapper {
    public ProductInWarehouse toProductInWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest) {
        ProductInWarehouse product = new ProductInWarehouse();
        product.setProductId(newProductInWarehouseRequest.getProductId());
        product.setDimension(DimensionMapper.mapToDimension(newProductInWarehouseRequest.getDimension()));
        product.setWeight(newProductInWarehouseRequest.getWeight());
        product.setFragile(newProductInWarehouseRequest.getFragile());
        product.setQuantity(newProductInWarehouseRequest.getQuantity());
        return product;
    }
}
