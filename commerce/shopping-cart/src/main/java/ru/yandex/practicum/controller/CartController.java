package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.service.CartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartController {
    final CartService cartService;

    @GetMapping
    public ShoppingCartDto getCart(@RequestParam String username) {
        log.info("Запрос на получение корзины пользователя с именем = {}", username);
        return cartService.getCart(username);
    }

    @PutMapping
    public ShoppingCartDto addProductToCart(@RequestParam String username,
                                            @RequestBody Map<UUID, Long> products) {
        log.info("Запрос на добавление товаров = {} в корзину пользователя с именем = {}", products, username);
        return cartService.addProductToCart(username, products);
    }

    @DeleteMapping
    public void deleteUserCart(@RequestParam String username) {
        log.info("Запрос на удаление корзины пользователя с именем = {}", username);
        cartService.deleteUserCart(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeFromCart(@RequestParam String username,
                                          @RequestBody List<UUID> productIds) {
        log.info("Запрос на удаление товаров = {} из корзины пользователя с именем = {}", productIds, username);
        return cartService.removeFromCart(username, productIds);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(@RequestParam String username,
                                                 @Valid @RequestBody ChangeProductQuantityRequest request) {
        log.info("Запрос на изменение количества товаров = {} в корзине пользователя с именем = {}", request, username);
        return cartService.changeProductQuantity(username, request);
    }
}