package ru.yandex.practicum.cart.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.cart.model.ShoppingCart;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

@UtilityClass
public class ShoppingCartMapper {
    public ShoppingCartDto toShoppingCartDto(ShoppingCart shoppingCart) {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setShoppingCartId(shoppingCart.getShoppingCartId());
        shoppingCartDto.setProducts(shoppingCart.getProductQuantityMap());
        return shoppingCartDto;
    }

    public ShoppingCart toShoppingCart(ShoppingCartDto shoppingCartDto) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setShoppingCartId(shoppingCartDto.getShoppingCartId());
        return shoppingCart;
    }
}
