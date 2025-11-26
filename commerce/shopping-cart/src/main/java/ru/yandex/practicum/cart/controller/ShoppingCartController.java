package ru.yandex.practicum.cart.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.service.ShoppingCartService;
import ru.yandex.practicum.contract.shopping.cart.ShoppingCartContract;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCartController implements ShoppingCartContract {
    final ShoppingCartService shoppingCartService;

    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String username) {
        return shoppingCartService.getShoppingCart(username);
    }

    @PutMapping
    public ShoppingCartDto addProductToShoppingCart(@RequestParam String username, @RequestBody Map<UUID, Integer> products) {
        return shoppingCartService.addProductToShoppingCart(username, products);
    }

    @DeleteMapping
    public ShoppingCartDto deactivateCurrentShoppingCart(@RequestParam String username) {
        return shoppingCartService.deactivateCurrentShoppingCart(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeFromShoppingCart(@RequestParam String username, @RequestBody Set<UUID> uuids) {
        return shoppingCartService.removeFromShoppingCart(username, uuids);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(@RequestParam String username,
                                                 @Validated @RequestBody ChangeProductQuantityRequest changeProductQuantityRequest) {
        return shoppingCartService.changeProductQuantity(username, changeProductQuantityRequest);
    }
}
