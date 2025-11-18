package ru.yandex.practicum.exception;

public class ProductInShoppingCartLowQuantityWarehouse extends RuntimeException {
    public ProductInShoppingCartLowQuantityWarehouse(String message) {
        super(message);
    }
}
