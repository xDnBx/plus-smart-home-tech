package ru.yandex.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.feign.CartClient;
import ru.yandex.practicum.service.CartService;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartController implements CartClient {
    final CartService cartService;

    @Override
    public ShoppingCartDto getCart(String username) {
        log.info("Запрос на получение корзины пользователя с именем = {}", username);
        return cartService.getCart(username);
    }

    @Override
    public ShoppingCartDto addProductToCart(String username, Map<UUID, Long> products) {
        log.info("Запрос на добавление товаров = {} в корзину пользователя с именем = {}", products, username);
        return cartService.addProductToCart(username, products);
    }

    @Override
    public void deleteUserCart(String username) {
        log.info("Запрос на удаление корзины пользователя с именем = {}", username);
        cartService.deleteUserCart(username);
    }

    @Override
    public ShoppingCartDto removeFromCart(String username, HashSet<UUID> productIds) {
        log.info("Запрос на удаление товаров = {} из корзины пользователя с именем = {}", productIds, username);
        return cartService.removeFromCart(username, productIds);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        log.info("Запрос на изменение количества товаров = {} в корзине пользователя с именем = {}", request, username);
        return cartService.changeProductQuantity(username, request);
    }
}