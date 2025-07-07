package ru.yandex.practicum.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.feign.fallback.WarehouseClientFallback;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallback = WarehouseClientFallback.class)
public interface WarehouseClient {
    @PutMapping
    void newProductInWarehouse(@Valid @RequestBody NewProductInWarehouseRequest request) throws FeignException;

    @PostMapping("/shipped")
    void shipToDelivery(@Valid @RequestBody ShippedToDeliveryRequest request) throws FeignException;

    @PostMapping("/return")
    void acceptReturn(@RequestBody Map<UUID, Long> returnedProducts) throws FeignException;

    @PostMapping("/check")
    BookedProductsDto checkProductQuantity(@Valid @RequestBody ShoppingCartDto shoppingCart) throws FeignException;

    @PostMapping("/assembly")
    BookedProductsDto assembleProducts(@Valid @RequestBody AssemblyProductsForOrderRequest request) throws FeignException;

    @PostMapping("/add")
    void addProductToWarehouse(@Valid @RequestBody AddProductToWarehouseRequest request) throws FeignException;

    @GetMapping("/address")
    AddressDto getWarehouseAddress() throws FeignException;
}