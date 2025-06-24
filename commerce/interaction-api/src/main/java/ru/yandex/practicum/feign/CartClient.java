package ru.yandex.practicum.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface CartClient {
    @GetMapping
    ShoppingCartDto getCart(@RequestParam String username) throws FeignException;

    @PutMapping
    ShoppingCartDto addProductToCart(@RequestParam String username,
                                     Map<UUID, Long> products) throws FeignException;

    @DeleteMapping
    void deleteUserCart(@RequestParam String username) throws FeignException;

    @PostMapping("/remove")
    ShoppingCartDto removeFromCart(@RequestParam String username,
                                   HashSet<UUID> productIds) throws FeignException;

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam String username,
                                          @Valid ChangeProductQuantityRequest request) throws FeignException;
}