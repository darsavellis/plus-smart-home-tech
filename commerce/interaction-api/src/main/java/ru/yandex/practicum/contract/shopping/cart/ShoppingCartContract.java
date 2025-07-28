package ru.yandex.practicum.contract.shopping.cart;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ShoppingCartContract {
    ShoppingCartDto getShoppingCart(@RequestParam String username);

    ShoppingCartDto addProductToShoppingCart(@RequestParam String username, @RequestBody Map<UUID, Integer> products);

    ShoppingCartDto deactivateCurrentShoppingCart(@RequestParam String username);

    ShoppingCartDto removeFromShoppingCart(@RequestParam String username, @RequestBody Set<UUID> uuids);

    ShoppingCartDto changeProductQuantity(@RequestParam String username,
                                          @RequestBody ChangeProductQuantityRequest changeProductQuantityRequest);
}
