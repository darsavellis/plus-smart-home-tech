package ru.yandex.practicum.cart.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.cart.model.ShoppingCart;
import ru.yandex.practicum.cart.repository.ShoppingCartRepository;
import ru.yandex.practicum.cart.service.ShoppingCartService;
import ru.yandex.practicum.dto.cart.CartState;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCartServiceImpl implements ShoppingCartService {
    static final String CART_NOT_FOUND_MESSAGE = "Корзина покупок для пользователя '%s' не найдена";
    final ShoppingCartRepository shoppingCartRepository;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        log.info("Запрос на получение корзины покупок для пользователя: {}", username);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Корзина покупок не найдена для пользователя: {}", username);
                    return new NotFoundException(String.format(CART_NOT_FOUND_MESSAGE, username));
                });

        log.debug("Получена корзина покупок для пользователя {}: {}", username, shoppingCart);
        return ShoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products) {
        log.info("Запрос на добавление товаров в корзину пользователя: {}. Количество товаров: {}", username, products.size());
        log.debug("Список добавляемых товаров: {}", products);

        ShoppingCart shoppingCart = shoppingCartRepository.findByUsername(username).orElseGet(() -> {
            log.info("Создание новой корзины для пользователя: {}", username);
            ShoppingCart cart = new ShoppingCart();
            cart.setUsername(username);
            cart.setProductQuantityMap(new HashMap<>());
            return cart;
        });

        products.forEach((productId, quantity) -> {
            Integer currentQuantity = shoppingCart.getProductQuantityMap().get(productId);
            log.debug("Добавление товара в корзину: ID={}, текущее количество={}, добавляемое количество={}",
                    productId, currentQuantity, quantity);
            shoppingCart.getProductQuantityMap().merge(productId, quantity, Integer::sum);
        });

        log.debug("Сохранение корзины в репозиторий: {}", shoppingCart);
        shoppingCartRepository.save(shoppingCart);

        ShoppingCartDto result = ShoppingCartMapper.toShoppingCartDto(shoppingCart);
        log.info("Товары успешно добавлены в корзину пользователя: {}. Итоговое количество товаров: {}",
                username, result.getProducts().size());
        return result;
    }

    @Override
    @Transactional
    public ShoppingCartDto deactivateCurrentShoppingCart(String username) {
        log.info("Запрос на деактивацию корзины для пользователя: {}", username);

        ShoppingCart shoppingCart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Невозможно деактивировать корзину. Корзина для пользователя {} не найдена", username);
                    return new NotFoundException(String.format(CART_NOT_FOUND_MESSAGE, username));
                });

        log.debug("Текущее состояние корзины перед деактивацией: {}", shoppingCart.getCartState());
        shoppingCart.setCartState(CartState.DISABLED);
        log.debug("Состояние корзины изменено на: {}", CartState.DISABLED);

        shoppingCartRepository.save(shoppingCart);
        log.info("Корзина пользователя {} успешно деактивирована", username);

        return ShoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeFromShoppingCart(String username, Set<UUID> uuids) {
        log.info("Запрос на удаление товаров из корзины пользователя: {}. Количество удаляемых товаров: {}",
                username, uuids.size());
        log.debug("Список удаляемых товаров: {}", uuids);

        ShoppingCart shoppingCart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Невозможно удалить товары. Корзина для пользователя {} не найдена", username);
                    return new NotFoundException(String.format(CART_NOT_FOUND_MESSAGE, username));
                });

        int initialSize = shoppingCart.getProductQuantityMap().size();
        log.debug("Текущее количество товаров в корзине: {}", initialSize);

        uuids.forEach(uuid -> {
            log.debug("Удаление товара с ID {} из корзины", uuid);
            shoppingCart.getProductQuantityMap().remove(uuid);
        });

        log.debug("Сохранение обновленной корзины в репозиторий");
        shoppingCartRepository.save(shoppingCart);

        int removedCount = initialSize - shoppingCart.getProductQuantityMap().size();
        log.info("Успешно удалено {} товаров из корзины пользователя: {}", removedCount, username);

        return ShoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest changeProductQuantityRequest) {
        log.info("Запрос на изменение количества товара в корзине пользователя: {}. ID товара: {}, новое количество: {}",
                username, changeProductQuantityRequest.getProductId(), changeProductQuantityRequest.getNewQuantity());

        ShoppingCart shoppingCart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Невозможно изменить количество товара. Корзина для пользователя {} не найдена", username);
                    return new NotFoundException(String.format(CART_NOT_FOUND_MESSAGE, username));
                });

        UUID productId = changeProductQuantityRequest.getProductId();
        Integer oldQuantity = shoppingCart.getProductQuantityMap().get(productId);
        Integer newQuantity = changeProductQuantityRequest.getNewQuantity();

        log.debug("Изменение количества товара: ID={}, старое количество={}, новое количество={}",
                productId, oldQuantity, newQuantity);

        shoppingCart.getProductQuantityMap().replace(productId, newQuantity);
        log.debug("Сохранение обновленной корзины в репозиторий");
        shoppingCartRepository.save(shoppingCart);

        log.info("Количество товара с ID {} в корзине пользователя {} успешно изменено", productId, username);

        return ShoppingCartMapper.toShoppingCartDto(shoppingCart);
    }
}
