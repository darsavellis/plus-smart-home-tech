package ru.yandex.practicum.contract.shopping.cart;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ShoppingCartContract {
    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam String username);

    @PutMapping
    ShoppingCartDto addProductToShoppingCart(@RequestParam String username, @RequestBody Map<UUID, Integer> products);

    @DeleteMapping
    ShoppingCartDto deactivateCurrentShoppingCart(@RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto removeFromShoppingCart(@RequestParam String username, @RequestBody Set<UUID> uuids);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam String username,
                                          @Validated @RequestBody ChangeProductQuantityRequest changeProductQuantityRequest);
}
